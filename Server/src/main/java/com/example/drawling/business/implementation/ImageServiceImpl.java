package com.example.drawling.business.implementation;

import com.example.drawling.business.helper.HashingHelper;
import com.example.drawling.business.helper.TimeConverterHelper;
import com.example.drawling.business.interfaces.repository.ImageRepository;
import com.example.drawling.business.interfaces.service.ImageCachingService;
import com.example.drawling.business.interfaces.service.ImageService;
import com.example.drawling.domain.model.Image;
import com.example.drawling.exception.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import static com.example.drawling.constants.MyConstants.IMAGES_FILE_PATH;

@Service
public class ImageServiceImpl implements ImageService {

    private final ImageRepository imageRepository;
    private final ImageCachingService imageCachingService;
    private final HashingHelper hashingHelper;

    private final String dirImages;

    private static final long MAX_IMAGE_SIZE = 20 * 1024 * 1024L; // 20MB

    public ImageServiceImpl(ImageRepository imageRepository, ImageCachingService imageCachingService, HashingHelper hashingHelper) {
        this.imageRepository = imageRepository;
        this.imageCachingService = imageCachingService;
        this.hashingHelper = hashingHelper;

        this.dirImages = new File(IMAGES_FILE_PATH).getAbsolutePath() + "/";

    }


    public Image saveImage(MultipartFile file) {
        try {
            validateFile(file);

            File directory = new File(dirImages);
            String fileHash = hashingHelper.computeFileHash(file.getInputStream());


            String cachedImageName = imageCachingService.checkForCachedImageReturnName(fileHash);
            if (cachedImageName != null){
                return imageRepository.getImageByName(cachedImageName);
            }

            Image saved = imageCachingService.checkForSavedImage(file);
            if (saved != null){
                imageCachingService.cacheImage(fileHash, saved.getName());
                return imageRepository.getImageByName(saved.getName());
            }

            if (!directory.exists() && !directory.mkdirs()) {
                throw new ImageUploadException("Could not create directory for saving images.");
            }

            // If no existing file is found, save the new file
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            String filePath = dirImages + fileName;
            file.transferTo(new File(filePath));

            String imageType = file.getContentType();

            Image image = new Image(fileName, filePath, imageType, TimeConverterHelper.getCurrentTimeInUTC());

            return imageRepository.save(image);
        } catch (IOException e) {
            throw new ImageUploadException("Failed to save image: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new ImageUploadException("An unexpected error occurred: " + e.getMessage(), e);
        }
    }

    public Image saveImage(BufferedImage bufferedImage) {
        try {
            if (bufferedImage == null) {
                throw new IllegalArgumentException("Image cannot be null.");
            }

            File directory = new File(dirImages);
            String fileHash = hashingHelper.computeImageHash(bufferedImage);

            String cachedImageName = imageCachingService.checkForCachedImageReturnName(fileHash);
            if (cachedImageName != null) {
                return imageRepository.getImageByName(cachedImageName);
            }

            Image saved = imageCachingService.checkForSavedImage(bufferedImage);
            if (saved != null) {
                imageCachingService.cacheImage(fileHash, saved.getName());
                return imageRepository.getImageByName(saved.getName());
            }

            if (!directory.exists() && !directory.mkdirs()) {
                throw new ImageUploadException("Could not create directory for saving images.");
            }

            String fileName = System.currentTimeMillis() + "_DRAWING.png";
            String filePath = dirImages + fileName;

            ImageIO.write(bufferedImage, "png", new File(filePath));

            String imageType = "image/png";

            Image image = new Image(fileName, filePath, imageType, TimeConverterHelper.getCurrentTimeInUTC());

            return imageRepository.save(image);
        } catch (IOException e) {
            throw new ImageUploadException("Failed to save image: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new ImageUploadException("An unexpected error occurred: " + e.getMessage(), e);
        }
    }




    public Image getById(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("ID must be greater than zero.");
        }
        return imageRepository.getById(id);
    }

    public List<Image> getAllImages() {
        try {
            List<Image> images = imageRepository.getAllImages();

            if (images.isEmpty()) {
                throw new ImageNotFoundException("No images found.");
            }

            return images;
        } catch (ImageNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new ImageUploadException("Error retrieving images", e);
        }
    }





    private void validateFile(MultipartFile file) {
        if (file == null) {
            throw new ImageValidationException("File cannot be null.");
        }

        if (file.isEmpty()) {
            throw new ImageValidationException("File is empty.");
        }

        if (file.getSize() > MAX_IMAGE_SIZE) {
            throw new ImageSizeExceededException("File size exceeds the maximum limit of 20MB.");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new UnsupportedImageTypeException("File type is not supported. Please upload an image.");
        }
    }


}
