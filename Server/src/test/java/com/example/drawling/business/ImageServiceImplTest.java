package com.example.drawling.business;

import com.example.drawling.business.implementation.ImageServiceImpl;
import com.example.drawling.business.interfaces.repository.ImageRepository;
import com.example.drawling.exception.ImageNotFoundException;
import com.example.drawling.exception.ImageUploadException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class ImageServiceImplTest {

    @Mock
    private ImageRepository imageRepository;

    @InjectMocks
    private ImageServiceImpl imageService;


    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSaveImage_NullFile() {
        Exception exception = assertThrows(ImageUploadException.class, () -> {
            imageService.saveImage((MultipartFile) null);
        });

        assertEquals("An unexpected error occurred: File cannot be null.", exception.getMessage());
    }

    @Test
    void testSaveImage_EmptyFile() {
        MockMultipartFile file = new MockMultipartFile("image", "test.jpg", "image/jpeg", new byte[0]);

        Exception exception = assertThrows(ImageUploadException.class, () -> {
            imageService.saveImage(file);
        });

        assertEquals("An unexpected error occurred: File is empty.", exception.getMessage());
    }

    @Test
    void testSaveImage_FileTooLarge() {
        MockMultipartFile file = new MockMultipartFile("image", "large.jpg", "image/jpeg", new byte[(int) (21 * 1024 * 1024)]);

        Exception exception = assertThrows(ImageUploadException.class, () -> {
            imageService.saveImage(file);
        });

        assertEquals("An unexpected error occurred: File size exceeds the maximum limit of 20MB.", exception.getMessage());
    }

    @Test
    void testSaveImage_UnsupportedFileType() {
        MockMultipartFile file = new MockMultipartFile("image", "test.txt", "text/plain", "text content".getBytes());

        Exception exception = assertThrows(ImageUploadException.class, () -> {
            imageService.saveImage(file);
        });

        assertEquals("An unexpected error occurred: File type is not supported. Please upload an image.", exception.getMessage());
    }



    @Test
    void testGetById_InvalidId() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            imageService.getById(0);
        });

        assertEquals("ID must be greater than zero.", exception.getMessage());
    }


    @Test
    void testGetAllImages_Exception() {
        lenient().when(imageRepository.getAllImages()).thenThrow(new RuntimeException("Database error"));

        Exception exception = assertThrows(ImageNotFoundException.class, () -> {
            imageService.getAllImages();
        });

        assertEquals("No images found.", exception.getMessage());
    }

}
