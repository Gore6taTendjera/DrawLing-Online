package com.example.drawling.business.implementation;

import com.example.drawling.business.helper.HashingHelper;
import com.example.drawling.business.helper.TimeConverterHelper;
import com.example.drawling.business.interfaces.service.ImageCachingService;
import com.example.drawling.domain.model.CachedImage;
import com.example.drawling.domain.model.Image;
import com.example.drawling.exception.ImageCacheException;
import com.example.drawling.exception.ImageCachingException;
import com.example.drawling.exception.ImageUploadException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

@Service
public class ImageCachingServiceImpl implements ImageCachingService {
    private final HashingHelper hashingHelper;
    private final String dirImages;
    private final String dirCachedImages;

    public ImageCachingServiceImpl(HashingHelper hashingHelper) {
        this.hashingHelper = hashingHelper;
        this.dirImages = new File("src/main/resources/images/").getAbsolutePath() + "/";
        this.dirCachedImages = new File("src/main/resources/json/cached_images.json").getAbsolutePath() + "/";
    }

    public String checkForCachedImageReturnName(String fileHash) {
        if (fileHash == null || fileHash.isEmpty()) {
            throw new IllegalArgumentException("File hash cannot be null or empty.");
        }
        try {
            List<CachedImage> cachedImages = readCachedImages();
            for (CachedImage cachedImage : cachedImages) {
                if (cachedImage.getHash().equals(fileHash)) {
                    return cachedImage.getName();
                }
            }
            return null;
        } catch (Exception e) {
            throw new ImageCacheException("Error checking for cached image: " + e.getMessage(), e);
        }
    }

    public String cacheImage(String fileHash, String fileName) {
        if (fileHash == null || fileHash.isEmpty()) {
            throw new IllegalArgumentException("File hash cannot be null or empty.");
        }
        if (fileName == null || fileName.isEmpty()) {
            throw new IllegalArgumentException("File name cannot be null or empty.");
        }
        CachedImage newCachedImage = new CachedImage();
        newCachedImage.setName(fileName);
        newCachedImage.setHash(fileHash);

        List<CachedImage> cachedImages = readCachedImages();
        cachedImages.add(newCachedImage);
        writeCachedImages(cachedImages);
        return fileHash;
    }


    public Image checkForSavedImage(MultipartFile file) {
        return checkForSavedImageInternal(() -> hashingHelper.computeFileHash(file.getInputStream()), "image/png");
    }

    public Image checkForSavedImage(BufferedImage bufferedImage) {
        return checkForSavedImageInternal(() -> hashingHelper.computeImageHash(bufferedImage), "image/png");
    }

    private Image checkForSavedImageInternal(HashFunction hashFunction, String imageType) {
        try {
            File directory = new File(dirImages);
            ensureDirectoryExists(directory);

            String fileHash = hashFunction.computeHash();
            File[] existingFiles = directory.listFiles();

            if (existingFiles != null) {
                for (File existingFile : existingFiles) {
                    if (existingFile.isFile() && fileHash.equals(hashingHelper.computeFileHash(Files.newInputStream(existingFile.toPath())))) {
                        return new Image(existingFile.getName(), existingFile.getAbsolutePath(), imageType, TimeConverterHelper.getCurrentTimeInUTC());
                    }
                }
            } else {
                throw new ImageUploadException("Failed to list files in the directory.");
            }

            return null;

        } catch (IOException e) {
            throw new ImageUploadException("Failed to check for the same image: " + e.getMessage(), e);
        }
    }

    private void ensureDirectoryExists(File directory) {
        if (!directory.exists()) {
            boolean created = directory.mkdirs();
            if (!created) {
                throw new ImageUploadException("Failed to create image directory: " + directory.getAbsolutePath());
            }
        }
    }

    private List<CachedImage> readCachedImages() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            File cachedFile = new File(dirCachedImages);
            if (cachedFile.exists() && cachedFile.length() > 0) {
                return objectMapper.readValue(cachedFile, new TypeReference<List<CachedImage>>() {});
            }
            return new ArrayList<>();
        } catch (IOException e) {
            throw new ImageUploadException("Failed to read cached images: " + e.getMessage(), e);
        }
    }

    private void writeCachedImages(List<CachedImage> cachedImages) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            File cachedFile = new File(dirCachedImages);
            objectMapper.writeValue(cachedFile, cachedImages);
        } catch (IOException e) {
            throw new ImageCachingException("Failed to write cached images: " + e.getMessage(), e);
        }
    }

    @FunctionalInterface
    private interface HashFunction {
        String computeHash() throws IOException;
    }
}
