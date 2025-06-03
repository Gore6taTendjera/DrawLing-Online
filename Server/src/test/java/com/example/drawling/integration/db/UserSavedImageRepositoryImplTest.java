package com.example.drawling.integration.db;

import com.example.drawling.business.interfaces.repository.ImageRepository;
import com.example.drawling.business.interfaces.repository.UserRepository;
import com.example.drawling.domain.entity.ImageEntity;
import com.example.drawling.domain.entity.SavedImageEntity;
import com.example.drawling.domain.entity.UserEntity;
import com.example.drawling.domain.model.Image;
import com.example.drawling.domain.model.SavedImage;
import com.example.drawling.domain.model.User;
import com.example.drawling.exception.ImageNotFoundException;
import com.example.drawling.exception.SavedImageException;
import com.example.drawling.mapper.ImageMapper;
import com.example.drawling.mapper.SavedImageMapper;
import com.example.drawling.mapper.UserMapper;
import com.example.drawling.repository.implementation.UserSavedImageRepositoryImpl;
import com.example.drawling.repository.jpa.SavedImageRepositoryJPA;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserSavedImageRepositoryImplTest {

    @Mock
    private ImageMapper imageMapper;

    @Mock
    private UserMapper userMapper;

    @Mock
    private SavedImageRepositoryJPA savedImageRepositoryJPA;

    @Mock
    private SavedImageMapper savedImageMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ImageRepository imageRepository;

    @InjectMocks
    private UserSavedImageRepositoryImpl userSavedImageRepository;

    private User user;
    private UserEntity userEntity;
    private Image image;
    private SavedImage savedImage;
    private SavedImageEntity savedImageEntity;
    private ImageEntity imageEntity;

    @BeforeEach
    void setUp() {
        user = new User(1, "displayName", "email@example.com", "password", "username");
        userEntity = new UserEntity(1, "displayName", "email@example.com", "password", "username");
        image = new Image(1, "imageUrl", "imageDescription");
        savedImage = new SavedImage(1, user, image);
        savedImageEntity = new SavedImageEntity();
        imageEntity = new ImageEntity();
    }

    @Test
    void addUserSavedImage_ShouldReturnSavedImage_WhenImageIsSavedSuccessfully() {
        // Arrange
        when(userRepository.getById(1)).thenReturn(user);
        when(imageRepository.getById(1)).thenReturn(image);

        UserEntity userEntity2 = userMapper.toEntity(user);
        when(userMapper.toEntity(user)).thenReturn(userEntity2);

        ImageEntity imageEntity2 = imageMapper.toEntity(image);
        when(imageMapper.toEntity(image)).thenReturn(imageEntity2);

        when(savedImageRepositoryJPA.save(any(SavedImageEntity.class))).thenReturn(savedImageEntity);
        when(savedImageMapper.toModel(savedImageEntity)).thenReturn(savedImage);

        // Act
        SavedImage result = userSavedImageRepository.addUserSavedImage(1, 1);

        // Assert
        assertNotNull(result);
        assertEquals(savedImage.getId(), result.getId());
        verify(savedImageRepositoryJPA, times(1)).save(any(SavedImageEntity.class));
    }

    @Test
    void addUserSavedImage_ShouldThrowSavedImageException_WhenDataAccessExceptionOccurs() {
        when(userRepository.getById(1)).thenReturn(user);
        when(imageRepository.getById(1)).thenReturn(image);
        when(userMapper.toEntity(user)).thenReturn(userEntity);
        when(imageMapper.toEntity(image)).thenReturn(imageEntity);
        when(savedImageRepositoryJPA.save(any(SavedImageEntity.class))).thenThrow(new DataAccessException("Error") {});

        // Act & Assert
        assertThrows(SavedImageException.class, () -> userSavedImageRepository.addUserSavedImage(1, 1));
    }


    @Test
    void getUserSavedImagesByUserId_ShouldReturnListOfImages_WhenImagesExist() {
        when(savedImageRepositoryJPA.findUserSavedImagesByUserId(1)).thenReturn(List.of(imageEntity));
        when(imageMapper.toModel(any(ImageEntity.class))).thenReturn(image);

        List<Image> images = userSavedImageRepository.getUserSavedImagesByUserId(1);

        assertNotNull(images);
        assertEquals(1, images.size());
    }

    @Test
    void getUserSavedImagesByUserId_ShouldThrowImageNotFoundException_WhenNoImagesFound() {
        when(savedImageRepositoryJPA.findUserSavedImagesByUserId(1)).thenReturn(Collections.emptyList());

        assertThrows(ImageNotFoundException.class, () -> userSavedImageRepository.getUserSavedImagesByUserId(1));
    }

    @Test
    void getUserSavedImagesByUserId_ShouldThrowIllegalArgumentException_WhenUserIdIsZeroOrNegative() {
        assertThrows(IllegalArgumentException.class, () -> userSavedImageRepository.getUserSavedImagesByUserId(0));
        assertThrows(IllegalArgumentException.class, () -> userSavedImageRepository.getUserSavedImagesByUserId(-1));
    }


    @Test
    void getUserSavedImagesByUserId_ShouldThrowImageNotFoundException_WhenDataAccessExceptionOccurs() {
        when(savedImageRepositoryJPA.findUserSavedImagesByUserId(1)).thenThrow(new DataAccessException("Error") {
        });

        assertThrows(ImageNotFoundException.class, () -> userSavedImageRepository.getUserSavedImagesByUserId(1));
    }
}