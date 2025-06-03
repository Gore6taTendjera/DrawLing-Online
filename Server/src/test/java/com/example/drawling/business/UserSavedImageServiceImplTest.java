package com.example.drawling.business;

import com.example.drawling.business.helper.HashingHelper;
import com.example.drawling.business.helper.TimeConverterHelper;
import com.example.drawling.business.implementation.UserSavedImageServiceImpl;
import com.example.drawling.business.interfaces.repository.ImageRepository;
import com.example.drawling.business.interfaces.repository.UserSavedImageRepository;
import com.example.drawling.business.interfaces.service.ImageCachingService;
import com.example.drawling.business.interfaces.service.ImageService;
import com.example.drawling.domain.model.Image;
import com.example.drawling.domain.model.SavedImage;
import com.example.drawling.exception.ImageNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserSavedImageServiceImplTest {

    @Mock
    private ImageService imageService;

    @Mock
    private UserSavedImageRepository userSavedImageRepository;

    @InjectMocks
    private UserSavedImageServiceImpl userSavedImageService;

    private MultipartFile mockFile;
    private Image mockImage;
    private SavedImage mockSavedImage;


    @Mock
    private HashingHelper hashingHelper;

    @Mock
    private ImageCachingService imageCachingService;

    @Mock
    private ImageRepository imageRepository;

    @Mock
    private TimeConverterHelper timeConverterHelper;


    @BeforeEach
    public void setUp() {
        mockFile = mock(MultipartFile.class);
        mockImage = new Image();
        mockImage.setId(1);
        mockSavedImage = new SavedImage();
        mockSavedImage.setImage(mockImage);
    }


    @Test
    void testSaveUserDrawing_Success() {
        int userId = 1;

        when(imageService.saveImage(mockFile)).thenReturn(mockImage);
        when(userSavedImageRepository.addUserSavedImage(userId, mockImage.getId())).thenReturn(mockSavedImage);

        int savedImageId = userSavedImageService.saveUserDrawing(userId, mockFile);

        assertEquals(mockImage.getId(), savedImageId);
        verify(imageService).saveImage(mockFile);
        verify(userSavedImageRepository).addUserSavedImage(userId, mockImage.getId());
    }

    @Test
    void testSaveUserDrawing_InvalidUserId() {
        int userId = 0; // Invalid user ID

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userSavedImageService.saveUserDrawing(userId, mockFile);
        });

        assertEquals("ID must be greater than zero.", exception.getMessage());
    }

    @Test
    void testGetUserSavedDrawings_Success() {
        int userId = 1;

        when(userSavedImageRepository.getUserSavedImagesByUserId(userId)).thenReturn(Collections.singletonList(mockImage));

        List<Image> images = userSavedImageService.getUserSavedDrawings(userId);

        assertNotNull(images);
        assertEquals(1, images.size());
        assertEquals(mockImage.getId(), images.get(0).getId());
        verify(userSavedImageRepository).getUserSavedImagesByUserId(userId);
    }

    @Test
    void testGetUserSavedDrawings_NoImagesFound() {
        int userId = 1;

        when(userSavedImageRepository.getUserSavedImagesByUserId(userId)).thenReturn(Collections.emptyList());

        ImageNotFoundException exception = assertThrows(ImageNotFoundException.class, () -> {
            userSavedImageService.getUserSavedDrawings(userId);
        });

        assertEquals("No saved drawings found for user id: " + userId, exception.getMessage());
    }

    @Test
    void testGetUserSavedDrawings_InvalidUserId() {
        int userId = 0; // Invalid user ID

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userSavedImageService.getUserSavedDrawings(userId);
        });

        assertEquals("User ID must be greater than zero.", exception.getMessage());
    }


}
