package com.example.drawling.business.implementation;

import com.example.drawling.business.interfaces.repository.ProfilePictureRepository;
import com.example.drawling.business.interfaces.service.ImageService;
import com.example.drawling.business.interfaces.service.ProfilePictureService;
import com.example.drawling.domain.model.Image;
import com.example.drawling.exception.ImageNotFoundException;
import com.example.drawling.exception.ImageUploadException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ProfilePictureServiceImpl implements ProfilePictureService {
    private final ImageService imageService;
    private final ProfilePictureRepository profilePictureRepository;

    public ProfilePictureServiceImpl(ImageService imageService, ProfilePictureRepository profilePictureRepository) {
        this.imageService = imageService;
        this.profilePictureRepository = profilePictureRepository;
    }


    public Image saveUserProfilePicture(int userId, MultipartFile file) {
        if (userId <= 0) {
            throw new IllegalArgumentException("ID must be greater than zero.");
        }

        try {
            Image image = imageService.saveImage(file);
            profilePictureRepository.setUserProfilePicture(userId, image.getId());
            return image;
        } catch (ImageUploadException e) {
            throw e;
        } catch (Exception e) {
            throw new ImageUploadException("Error saving user profile picture for user id: " + userId, e);
        }
    }

    public Image getUserProfilePictureById(int userId) {
        try {
            if (userId <= 0) {
                throw new IllegalArgumentException("User ID must be greater than zero.");
            }

            return profilePictureRepository.getUserProfilePictureById(userId);
        } catch (ImageNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new ImageUploadException("Error retrieving image for user id: " + userId, e);
        }
    }

    public int getUserProfilePictureIdByUserId(int userId) {
        profilePictureRepository.getUserProfilePictureById(userId);
        return profilePictureRepository.getUserProfilePictureById(userId).getId();
    }

}
