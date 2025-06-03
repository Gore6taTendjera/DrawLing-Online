package com.example.drawling.repository.implementation;

import com.example.drawling.business.interfaces.repository.ImageRepository;
import com.example.drawling.business.interfaces.repository.UserRepository;
import com.example.drawling.business.interfaces.repository.UserSavedImageRepository;
import com.example.drawling.domain.entity.ImageEntity;
import com.example.drawling.domain.entity.SavedImageEntity;
import com.example.drawling.domain.model.Image;
import com.example.drawling.domain.model.SavedImage;
import com.example.drawling.domain.model.User;
import com.example.drawling.exception.ImageNotFoundException;
import com.example.drawling.exception.SavedImageException;
import com.example.drawling.mapper.ImageMapper;
import com.example.drawling.mapper.SavedImageMapper;
import com.example.drawling.mapper.UserMapper;
import com.example.drawling.repository.jpa.SavedImageRepositoryJPA;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class UserSavedImageRepositoryImpl implements UserSavedImageRepository {
    private final ImageMapper imageMapper;
    private final UserMapper userMapper;
    private final SavedImageRepositoryJPA savedImageRepositoryJPA;
    private final SavedImageMapper savedImageMapper;
    private final UserRepository userRepository;
    private final ImageRepository imageRepository;

    public UserSavedImageRepositoryImpl(ImageMapper imageMapper, UserMapper userMapper, SavedImageRepositoryJPA savedImageRepositoryJPA, SavedImageMapper savedImageMapper, UserRepository userRepository, ImageRepository imageRepository) {
        this.imageMapper = imageMapper;
        this.userMapper = userMapper;
        this.savedImageRepositoryJPA = savedImageRepositoryJPA;
        this.savedImageMapper = savedImageMapper;
        this.userRepository = userRepository;
        this.imageRepository = imageRepository;
    }


    @Transactional
    public SavedImage addUserSavedImage(int userId, int imageId) {
        SavedImage savedImage;
        try {
            User user = userRepository.getById(userId);
            Image image = imageRepository.getById(imageId);

            SavedImageEntity savedImageEntity = new SavedImageEntity();
            savedImageEntity.setUser(userMapper.toEntity(user));
            savedImageEntity.setImage(imageMapper.toEntity(image));

            SavedImageEntity savedEntity = savedImageRepositoryJPA.save(savedImageEntity);
            savedImage = savedImageMapper.toModel(savedEntity);
        } catch (DataAccessException e) {
            throw new SavedImageException("Failed to save the image: " + e.getMessage(), e);
        }
        return savedImage;
    }

    @Transactional(readOnly = true)
    public List<Image> getUserSavedImagesByUserId(int userId) {
        if (userId <= 0) {
            throw new IllegalArgumentException("User ID must be greater than zero.");
        }

        try {
            List<ImageEntity> imageEntities = savedImageRepositoryJPA.findUserSavedImagesByUserId(userId);
            if (imageEntities.isEmpty()) {
                throw new ImageNotFoundException("No saved images found for user id: " + userId);
            }
            return imageEntities.stream()
                    .map(imageMapper::toModel)
                    .toList();
        } catch (DataAccessException e) {
            throw new ImageNotFoundException("Error retrieving saved images for user id: " + userId, e);
        }
    }
}