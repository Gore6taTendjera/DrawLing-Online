package com.example.drawling.integration.db;

import com.example.drawling.domain.entity.ImageEntity;
import com.example.drawling.domain.model.Image;
import com.example.drawling.exception.FailedToSetUserProfilePictureException;
import com.example.drawling.exception.ImageNotFoundException;
import com.example.drawling.mapper.ImageMapper;
import com.example.drawling.repository.implementation.ProfilePictureRepositoryImpl;
import com.example.drawling.repository.jpa.ImageRepositoryJPA;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataAccessException;

import java.time.ZonedDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProfilePictureRepositoryImplTest {

    @InjectMocks
    private ProfilePictureRepositoryImpl profilePictureRepository;

    @Mock
    private ImageMapper imageMapper;

    @Mock
    private ImageRepositoryJPA imageRepositoryJPA;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSetUserProfilePicture_Success() {
        int userId = 1;
        int imageId = 1;

        when(imageRepositoryJPA.setImageProfilePicture(userId, imageId)).thenReturn(1);

        assertDoesNotThrow(() -> profilePictureRepository.setUserProfilePicture(userId, imageId));

        verify(imageRepositoryJPA, times(1)).setImageProfilePicture(userId, imageId);
    }

    @Test
    void testSetUserProfilePicture_Failure() {
        int userId = 1;
        int imageId = 1;

        when(imageRepositoryJPA.setImageProfilePicture(userId, imageId)).thenReturn(0);

        Exception exception = assertThrows(FailedToSetUserProfilePictureException.class, () -> {
            profilePictureRepository.setUserProfilePicture(userId, imageId);
        });

        assertEquals("Failed to set user profile picture.", exception.getMessage());
        verify(imageRepositoryJPA, times(1)).setImageProfilePicture(userId, imageId);
    }

    @Test
    void testSetUserProfilePicture_DataAccessException() {
        int userId = 1;
        int imageId = 1;

        when(imageRepositoryJPA.setImageProfilePicture(userId, imageId)).thenThrow(new DataAccessException("Database error") {});

        Exception exception = assertThrows(FailedToSetUserProfilePictureException.class, () -> {
            profilePictureRepository.setUserProfilePicture(userId, imageId);
        });

        assertTrue(exception.getMessage().contains("Failed to set user profile picture: Database error"));
        verify(imageRepositoryJPA, times(1)).setImageProfilePicture(userId, imageId);
    }

    @Test
    void testGetUserProfilePictureById_Success() {
        int userId = 1;

        ImageEntity imageEntity = new ImageEntity();
        imageEntity.setId(1);
        imageEntity.setName("Sample Image");
        imageEntity.setPath("http://example.com/image.jpg");
        imageEntity.setType("image/jpeg");
        imageEntity.setCreatedAt(ZonedDateTime.now());

        when(imageRepositoryJPA.findUserProfilePictureById(userId)).thenReturn(Optional.of(imageEntity));

        Image image = new Image();
        image.setId(imageEntity.getId());
        image.setName(imageEntity.getName());
        image.setPath(imageEntity.getPath());
        image.setType(imageEntity.getType());
        image.setCreatedAt(imageEntity.getCreatedAt());

        when(imageMapper.toModel(imageEntity)).thenReturn(image);

        Image result = profilePictureRepository.getUserProfilePictureById(userId);

        assertNotNull(result);
        assertEquals(image.getId(), result.getId());
        assertEquals(image.getName(), result.getName());
        assertEquals(image.getPath(), result.getPath());
        assertEquals(image.getType(), result.getType());
        assertEquals(image.getCreatedAt(), result.getCreatedAt());

        verify(imageRepositoryJPA, times(1)).findUserProfilePictureById(userId);
        verify(imageMapper, times(1)).toModel(imageEntity);
    }



    @Test
    void testGetUserProfilePictureById_ImageNotFound() {
        int userId = 1;

        when(imageRepositoryJPA.findUserProfilePictureById(userId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(ImageNotFoundException.class, () -> {
            profilePictureRepository.getUserProfilePictureById(userId);
        });

        assertEquals("Profile picture not found for user id: " + userId, exception.getMessage());
        verify(imageRepositoryJPA, times(1)).findUserProfilePictureById(userId);
    }

    @Test
    void testGetUserProfilePictureById_DataAccessException() {
        int userId = 1;

        when(imageRepositoryJPA.findUserProfilePictureById(userId)).thenThrow(new DataAccessException("Database error") {});

        Exception exception = assertThrows(ImageNotFoundException.class, () -> {
            profilePictureRepository.getUserProfilePictureById(userId);
        });

        assertTrue(exception.getMessage().contains("Error retrieving profile picture for user id: " + userId));
        verify(imageRepositoryJPA, times(1)).findUserProfilePictureById(userId);
    }

    @Test
    void testSetUserProfilePicture_InvalidUserId() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            profilePictureRepository.setUserProfilePicture(0, 1);
        });
        assertEquals("User ID must be greater than zero.", exception.getMessage());
    }

    @Test
    void testSetUserProfilePicture_InvalidImageId() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            profilePictureRepository.setUserProfilePicture(1, 0);
        });
        assertEquals("Image ID must be greater than zero.", exception.getMessage());
    }

    @Test
    void testGetUserProfilePictureById_InvalidUserId() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            profilePictureRepository.getUserProfilePictureById(0);
        });
        assertEquals("User ID must be greater than zero.", exception.getMessage());
    }
}
