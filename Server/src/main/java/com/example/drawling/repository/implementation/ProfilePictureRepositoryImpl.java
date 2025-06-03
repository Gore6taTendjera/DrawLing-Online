package com.example.drawling.repository.implementation;

import com.example.drawling.business.interfaces.repository.ProfilePictureRepository;
import com.example.drawling.domain.model.Image;
import com.example.drawling.exception.FailedToSetUserProfilePictureException;
import com.example.drawling.exception.ImageNotFoundException;
import com.example.drawling.mapper.ImageMapper;
import com.example.drawling.repository.jpa.ImageRepositoryJPA;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class ProfilePictureRepositoryImpl implements ProfilePictureRepository {
    private final ImageMapper imageMapper;
    private final ImageRepositoryJPA imageRepositoryJPA;

    public ProfilePictureRepositoryImpl(ImageMapper imageMapper, ImageRepositoryJPA imageRepositoryJPA) {
        this.imageMapper = imageMapper;
        this.imageRepositoryJPA = imageRepositoryJPA;
    }

    @Transactional
    public void setUserProfilePicture(int userId, int imageId) {
        if (userId <= 0) {
            throw new IllegalArgumentException("User ID must be greater than zero.");
        }

        if (imageId <= 0) {
            throw new IllegalArgumentException("Image ID must be greater than zero.");
        }

        try {
            int result = imageRepositoryJPA.setImageProfilePicture(userId, imageId);
            if (result == 0) {
                throw new FailedToSetUserProfilePictureException("Failed to set user profile picture.");
            }
        } catch (DataAccessException e) {
            throw new FailedToSetUserProfilePictureException("Failed to set user profile picture: " + e.getMessage(), e);
        }
    }

    @Transactional(readOnly = true)
    public Image getUserProfilePictureById(int userId) {
        if (userId <= 0) {
            throw new IllegalArgumentException("User ID must be greater than zero.");
        }

        try {
            return imageRepositoryJPA.findUserProfilePictureById(userId)
                    .map(imageMapper::toModel)
                    .orElseThrow(() -> new ImageNotFoundException("Profile picture not found for user id: " + userId));
        } catch (DataAccessException e) {
            throw new ImageNotFoundException("Error retrieving profile picture for user id: " + userId, e);
        }
    }
}