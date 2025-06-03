package com.example.drawling.integration.api;

import com.example.drawling.application.controller.ProfilePictureController;
import com.example.drawling.business.interfaces.service.ProfilePictureService;
import com.example.drawling.domain.model.Image;
import com.example.drawling.exception.ImageNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class ProfilePictureControllerTest {

    @InjectMocks
    private ProfilePictureController profilePictureController;

    @Mock
    private ProfilePictureService profilePictureService;

    @Mock
    private MultipartFile file;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void uploadUserProfilePicture_FileIsEmpty_ReturnsBadRequest() {
        when(file.isEmpty()).thenReturn(true);

        ResponseEntity<String> response = profilePictureController.uploadUserProfilePicture(1, file);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("File is empty.", response.getBody());
    }

    @Test
    void uploadUserProfilePicture_Success_ReturnsImageUrl() {
        Image image = new Image();
        image.setId(1);
        when(file.isEmpty()).thenReturn(false);
        when(profilePictureService.saveUserProfilePicture(1, file)).thenReturn(image);

        ResponseEntity<String> response = profilePictureController.uploadUserProfilePicture(1, file);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("http://localhost:8080/api/images/1", response.getBody());
    }

    @Test
    void uploadUserProfilePicture_ImageNotFound_ReturnsNotFound() {
        when(file.isEmpty()).thenReturn(false);
        when(profilePictureService.saveUserProfilePicture(1, file)).thenThrow(new ImageNotFoundException("Image not found"));

        ResponseEntity<String> response = profilePictureController.uploadUserProfilePicture(1, file);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(null, response.getBody());
    }

    @Test
    void getUserProfilePicture_Success_ReturnsImageUrl() {
        Image image = new Image();
        image.setId(1);
        when(profilePictureService.getUserProfilePictureById(1)).thenReturn(image);

        ResponseEntity<String> response = profilePictureController.getUserProfilePicture(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("http://localhost:8080/api/images/1", response.getBody());
    }

    @Test
    void getUserProfilePicture_ImageNotFound_ReturnsNotFound() {
        when(profilePictureService.getUserProfilePictureById(1)).thenThrow(new ImageNotFoundException("Image not found"));

        ResponseEntity<String> response = profilePictureController.getUserProfilePicture(1);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(null, response.getBody());
    }

    @Test
    void getUserProfilePicture_RuntimeException_ReturnsInternalServerError() {
        when(profilePictureService.getUserProfilePictureById(1)).thenThrow(new RuntimeException("Some error"));

        ResponseEntity<String> response = profilePictureController.getUserProfilePicture(1);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(null, response.getBody());
    }
}