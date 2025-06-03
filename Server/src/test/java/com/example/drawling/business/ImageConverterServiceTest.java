package com.example.drawling.business;

import com.example.drawling.business.implementation.ImageConverterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

class ImageConverterServiceTest {

    private ImageConverterService imageConverterService;
    private MultipartFile mockFile;

    @BeforeEach
    public void setUp() {
        imageConverterService = new ImageConverterService();
        mockFile = Mockito.mock(MultipartFile.class);
    }


    @Test
    void testConvert_Success() throws IOException {
        // Arrange
        byte[] imageBytes = { /* some valid image data */ };
        ByteArrayInputStream inputStream = new ByteArrayInputStream(imageBytes);
        BufferedImage expectedImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);

        try (MockedStatic<ImageIO> mockedImageIO = mockStatic(ImageIO.class)) {
            mockedImageIO.when(() -> ImageIO.read(any(ByteArrayInputStream.class))).thenReturn(expectedImage);

            when(mockFile.isEmpty()).thenReturn(false);
            when(mockFile.getInputStream()).thenReturn(inputStream);

            // Act
            BufferedImage result = imageConverterService.convert(mockFile);

            // Assert
            assertNotNull(result);
            assertEquals(expectedImage, result);
        }
    }


    @Test
    void testConvert_EmptyFile() {
        // Arrange
        when(mockFile.isEmpty()).thenReturn(true);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            imageConverterService.convert(mockFile);
        });
        assertEquals("File is empty", exception.getMessage());
    }

    @Test
    void testConvert_IOException() throws IOException {
        // Arrange
        when(mockFile.isEmpty()).thenReturn(false);
        when(mockFile.getInputStream()).thenThrow(new IOException("I/O error"));

        // Act & Assert
        IOException exception = assertThrows(IOException.class, () -> {
            imageConverterService.convert(mockFile);
        });
        assertEquals("I/O error", exception.getMessage());
    }
}
