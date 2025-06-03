package com.example.drawling.integration.api;

import com.example.drawling.application.controller.UserSavedImagesController;
import com.example.drawling.business.implementation.ImageConverterService;
import com.example.drawling.business.interfaces.service.UserSavedImageService;
import com.example.drawling.domain.model.Image;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static com.example.drawling.constants.MyConstants.API_IMAGES_URL;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserSavedImagesControllerTest {

    @Mock
    private UserSavedImageService userSavedImageService;

    @Mock
    private ImageConverterService imageConverterService;

    @InjectMocks
    private UserSavedImagesController userSavedImagesController;

    @BeforeEach
    void setUp() {
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void testGetUserSavedDrawings_Success() {
        int userId = 1;
        Image image1 = new Image(1, "image1");
        Image image2 = new Image(2, "image2");
        when(userSavedImageService.getUserSavedDrawings(userId)).thenReturn(Arrays.asList(image1, image2));

        ResponseEntity<List<String>> response = userSavedImagesController.getUserSavedDrawings(userId);

        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertEquals(API_IMAGES_URL + "1", response.getBody().get(0));
        assertEquals(API_IMAGES_URL + "2", response.getBody().get(1));
        verify(userSavedImageService).getUserSavedDrawings(userId);
    }

    @Test
    void testGetUserSavedDrawings_EmptyList() {
        int userId = 1;
        when(userSavedImageService.getUserSavedDrawings(userId)).thenReturn(List.of());

        ResponseEntity<List<String>> response = userSavedImagesController.getUserSavedDrawings(userId);

        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
        verify(userSavedImageService).getUserSavedDrawings(userId);
    }

    @Test
    void testSaveUserDrawing_Success() throws IOException {
        int userId = 1;
        MockMultipartFile file = new MockMultipartFile("file", "image.png", "image/png", "dummy data".getBytes());
        when(userSavedImageService.saveUserDrawing(eq(userId), any(MockMultipartFile.class))).thenReturn(1);

        ResponseEntity<String> response = userSavedImagesController.saveUserDrawing(userId, file);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(API_IMAGES_URL + "1", response.getBody());
        verify(imageConverterService).convert(file);
        verify(userSavedImageService).saveUserDrawing(userId, file);
    }

    @Test
    void testSaveUserDrawing_FileEmpty() {
        int userId = 1;
        MockMultipartFile file = new MockMultipartFile("file", "image.png", "image/png", new byte[0]);

        ResponseEntity<String> response = userSavedImagesController.saveUserDrawing(userId, file);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("File is empty.", response.getBody());
        verifyNoInteractions(imageConverterService);
        verifyNoInteractions(userSavedImageService);
    }

    @Test
    void testSaveUserDrawing_InvalidUserId() {
        int userId = -1;
        MockMultipartFile file = new MockMultipartFile("file", "image.png", "image/png", "dummy data".getBytes());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userSavedImagesController.saveUserDrawing(userId, file);
        });

        assertEquals("ID must be greater than zero.", exception.getMessage());
        verifyNoInteractions(imageConverterService);
        verifyNoInteractions(userSavedImageService);
    }

    @Test
    void testSaveUserDrawing_IOException() throws IOException {
        int userId = 1;
        MockMultipartFile file = new MockMultipartFile("file", "image.png", "image/png", "dummy data".getBytes());
        doThrow(IOException.class).when(imageConverterService).convert(file);

        ResponseEntity<String> response = userSavedImagesController.saveUserDrawing(userId, file);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
        verify(imageConverterService).convert(file);
        verifyNoInteractions(userSavedImageService);
    }
}