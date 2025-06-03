package com.example.drawling.business.implementation;

import com.example.drawling.business.interfaces.repository.UserSavedImageRepository;
import com.example.drawling.business.interfaces.service.ImageService;
import com.example.drawling.business.interfaces.service.UserSavedImageService;
import com.example.drawling.domain.model.Image;
import com.example.drawling.domain.model.SavedImage;
import com.example.drawling.exception.ImageNotFoundException;
import com.example.drawling.exception.ImageUploadException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.util.List;

@Service
public class UserSavedImageServiceImpl implements UserSavedImageService {

    private final ImageService imageService;
    private final UserSavedImageRepository userSavedImageRepository;

    public UserSavedImageServiceImpl(ImageService imageService, UserSavedImageRepository userSavedImageRepository) {
        this.imageService = imageService;
        this.userSavedImageRepository = userSavedImageRepository;
    }

    public int saveUserDrawing(int userId, MultipartFile file) {
        if (userId <= 0) {
            throw new IllegalArgumentException("ID must be greater than zero.");
        }

        try {
            Image image = imageService.saveImage(file);
            SavedImage savedImage = userSavedImageRepository.addUserSavedImage(userId, image.getId());
            return savedImage.getImage().getId();
        } catch (ImageUploadException e) {
            throw new ImageUploadException("Failed to save user drawing for user id: " + userId, e);
        }
    }

    @Override
    public int saveUserDrawing(int userId, BufferedImage image) {
        if (userId <= 0) {
            throw new IllegalArgumentException("ID must be greater than zero.");
        }

        try {
            Image image1 = imageService.saveImage(image);
            SavedImage savedImage = userSavedImageRepository.addUserSavedImage(userId, image1.getId());
            return savedImage.getImage().getId();
        } catch (ImageUploadException e) {
            throw new ImageUploadException("Failed to save user drawing for user id: " + userId, e);
        }
    }


    public List<Image> getUserSavedDrawings(int userId) {
        if (userId <= 0) {
            throw new IllegalArgumentException("User ID must be greater than zero.");
        }

        try {
            List<Image> images = userSavedImageRepository.getUserSavedImagesByUserId(userId);
            if (images.isEmpty()) {
                throw new ImageNotFoundException("No saved drawings found for user id: " + userId);
            }

            return images;
        } catch (ImageNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new ImageUploadException("Error retrieving saved drawings for user id: " + userId, e);
        }
    }
}
