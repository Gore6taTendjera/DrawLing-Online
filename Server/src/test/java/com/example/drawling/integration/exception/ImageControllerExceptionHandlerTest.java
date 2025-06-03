package com.example.drawling.integration.exception;

import com.example.drawling.application.handler.exception.ImageControllerExceptionHandler;
import com.example.drawling.exception.FailedToSetUserProfilePictureException;
import com.example.drawling.exception.ImageNotFoundException;
import com.example.drawling.exception.ImageUploadException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class ImageControllerExceptionHandlerTest {

    @InjectMocks
    private ImageControllerExceptionHandler exceptionHandler;

    @Test
    void testHandleImageNotFoundException() {
        ImageNotFoundException exception = new ImageNotFoundException("Image not found");
        ResponseEntity<String> response = exceptionHandler.handleImageNotFoundException(exception);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Image not found", response.getBody());
    }

    @Test
    void testHandleImageUploadException() {
        ImageUploadException exception = new ImageUploadException("Image upload failed");
        ResponseEntity<String> response = exceptionHandler.handleImageUploadException(exception);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Image upload failed", response.getBody());
    }

    @Test
    void testHandleFailedToSetUserProfilePictureException() {
        FailedToSetUserProfilePictureException exception = new FailedToSetUserProfilePictureException("Failed to set profile picture");
        ResponseEntity<String> response = exceptionHandler.handleFailedToSetUserProfilePicture(exception);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Failed to set profile picture", response.getBody());
    }

    @Test
    void testHandleIOException() {
        IOException exception = new IOException("IO error occurred");
        ResponseEntity<String> response = exceptionHandler.handleIOException(exception);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("IO error occurred", response.getBody());
    }

    @Test
    void testHandleIllegalArgumentException() {
        IllegalArgumentException exception = new IllegalArgumentException("Invalid argument");
        ResponseEntity<String> response = exceptionHandler.handleIllegalArgumentException(exception);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid argument", response.getBody());
    }
}