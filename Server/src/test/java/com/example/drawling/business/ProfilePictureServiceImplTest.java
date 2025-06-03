package com.example.drawling.business;

import com.example.drawling.business.implementation.ProfilePictureServiceImpl;
import com.example.drawling.business.interfaces.repository.ProfilePictureRepository;
import com.example.drawling.business.interfaces.service.ImageService;
import com.example.drawling.domain.model.Image;
import com.example.drawling.exception.ImageNotFoundException;
import com.example.drawling.exception.ImageUploadException;
import com.example.drawling.mapper.ImageMapper;
import com.example.drawling.repository.jpa.ImageRepositoryJPA;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProfilePictureServiceImplTest {

    @Mock
    private ImageService imageService;

    @Mock
    private ProfilePictureRepository profilePictureRepository;

    @InjectMocks
    private ProfilePictureServiceImpl profilePictureService;

    @Mock
    private ImageRepositoryJPA imageRepositoryJPA;

    @Mock
    private ImageMapper imageMapper;


    @BeforeEach
    void setUp() {
        // mocks
    }


    @Test
    void saveUserProfilePicture_ShouldThrowException_WhenUserIdIsInvalid() {
        // Arrange
        int userId = -1;
        MultipartFile file = mock(MultipartFile.class);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                profilePictureService.saveUserProfilePicture(userId, file));
        assertEquals("ID must be greater than zero.", exception.getMessage());
        verify(imageService, never()).saveImage(any(MultipartFile.class));
        verify(profilePictureRepository, never()).setUserProfilePicture(anyInt(), anyInt());
    }

    @Test
    void saveUserProfilePicture_ShouldCallRepository_WhenUserIdIsValid() {
        // Arrange
        int userId = 1;
        MultipartFile file = mock(MultipartFile.class);
        Image image = new Image(1, "imagePath", "imageType");

        when(imageService.saveImage(file)).thenReturn(image);

        // Act
        Image savedImage = profilePictureService.saveUserProfilePicture(userId, file);

        // Assert
        assertEquals(image, savedImage);
        verify(imageService, times(1)).saveImage(file);
        verify(profilePictureRepository, times(1)).setUserProfilePicture(userId, image.getId());
    }

    @Test
    void getUserProfilePictureById_ShouldThrowException_WhenUserIdIsInvalid() {
        // Arrange
        int userId = -1;

        // Act & Assert
        ImageUploadException exception = assertThrows(ImageUploadException.class, () ->
                profilePictureService.getUserProfilePictureById(userId));
        assertEquals("Error retrieving image for user id: -1", exception.getMessage());
        verify(profilePictureRepository, never()).getUserProfilePictureById(anyInt());
    }

    @Test
    void getUserProfilePictureById_ShouldReturnImage_WhenUserIdIsValid() {
        // Arrange
        int userId = 1;
        Image expectedImage = new Image(1, "imagePath", "imageType");

        when(profilePictureRepository.getUserProfilePictureById(userId)).thenReturn(expectedImage);

        // Act
        Image actualImage = profilePictureService.getUserProfilePictureById(userId);

        // Assert
        assertEquals(expectedImage, actualImage);
        verify(profilePictureRepository, times(1)).getUserProfilePictureById(userId);
    }

    @Test
    void getUserProfilePictureById_ShouldThrowImageUploadException_WhenImageNotFound() {
        // Arrange
        int userId = 1;
        when(profilePictureRepository.getUserProfilePictureById(userId)).thenThrow(new ImageNotFoundException("Image not found"));

        // Act & Assert
        ImageNotFoundException exception = assertThrows(ImageNotFoundException.class, () ->
                profilePictureService.getUserProfilePictureById(userId));
        assertEquals("Image not found", exception.getMessage());
        verify(profilePictureRepository, times(1)).getUserProfilePictureById(userId);
    }

    @Test
    void testGetUserProfilePictureIdByUserId() {
        // Arrange
        int userId = 1;
        int profilePictureId = 101;
        Image profilePicture = new Image(profilePictureId, "profile_picture.jpg", "/path/to/image", "image/jpeg");

        when(profilePictureRepository.getUserProfilePictureById(userId)).thenReturn(profilePicture);

        // Act
        int result = profilePictureService.getUserProfilePictureIdByUserId(userId);

        // Assert
        assertEquals(profilePictureId, result, "The profile picture ID should match the expected value.");
        verify(profilePictureRepository, times(2)).getUserProfilePictureById(userId);
    }




    @Test
    void testMockThrowsDataAccessException() {
        when(imageRepositoryJPA.findUserProfilePictureById(anyInt()))
                .thenThrow(new DataAccessException("Mocked database error") {});
        assertThrows(
                DataAccessException.class,
                () -> imageRepositoryJPA.findUserProfilePictureById(1)
        );
    }



}