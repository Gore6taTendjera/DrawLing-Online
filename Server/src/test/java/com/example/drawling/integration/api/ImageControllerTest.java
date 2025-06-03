package com.example.drawling.integration.api;

import com.example.drawling.application.controller.ImageController;
import com.example.drawling.business.interfaces.service.ImageService;
import com.example.drawling.domain.model.Image;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ImageControllerTest {

    @Mock
    private ImageService imageService;

    @InjectMocks
    private ImageController imageController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testUploadImage_Success() {
        // Arrange
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        Image savedImage = new Image();
        savedImage.setId(1);
        when(imageService.saveImage(file)).thenReturn(savedImage);

        // Act
        ResponseEntity<String> response = imageController.uploadImage(file);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("http://localhost:8080/api/images/1", response.getBody());
    }

    @Test
    void testUploadImage_EmptyFile() {
        // Arrange
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(true);

        // Act
        ResponseEntity<String> response = imageController.uploadImage(file);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("File is empty.", response.getBody());
    }

    @Test
    void testUploadImage_InvalidFileFormat() {
        // Arrange
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        when(imageService.saveImage(file)).thenThrow(new IllegalArgumentException("Invalid file format."));

        // Act
        ResponseEntity<String> response = imageController.uploadImage(file);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid file format.", response.getBody());
    }




    @Test
    void testGetImageById_InvalidId() {
        // Arrange
        int imageId = -1;

        // Act & Assert
        int finalImageId = imageId;
        assertThrows(IllegalArgumentException.class, () -> imageController.getImageById(finalImageId));
        // Arrange
        imageId = 1;
        when(imageService.getById(imageId)).thenThrow(new NoSuchElementException("Image not found."));

        // Act
        ResponseEntity<byte[]> response = imageController.getImageById(imageId);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testGetAllImages_Success() {
        // Arrange
        List<Image> images = new ArrayList<>();
        Image image1 = new Image();
        image1.setId(1);
        Image image2 = new Image();
        image2.setId(2);
        images.add(image1);
        images.add(image2);
        when(imageService.getAllImages()).thenReturn(images);

        // Act
        ResponseEntity<List<String>> response = imageController.getAllImages();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
        assertTrue(response.getBody().contains("http://localhost:8080/api/images/1"));
        assertTrue(response.getBody().contains("http://localhost:8080/api/images/2"));
    }

    @Test
    void testGetAllImages_NoContent() {
        // Arrange
        List<Image> images = new ArrayList<>();
        when(imageService.getAllImages()).thenReturn(images);

        // Act
        ResponseEntity<List<String>> response = imageController.getAllImages();

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
    }
}
