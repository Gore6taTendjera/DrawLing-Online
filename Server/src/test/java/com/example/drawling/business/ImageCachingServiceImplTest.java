package com.example.drawling.business;

import com.example.drawling.business.implementation.ImageCachingServiceImpl;
import com.example.drawling.domain.model.CachedImage;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
class ImageCachingServiceImplTest {

    @InjectMocks
    private ImageCachingServiceImpl imageCachingService;

    private String dirCachedImages;

    @BeforeEach
    public void setUp() {
        dirCachedImages = new File("src/main/resources/json/cached_images.json").getAbsolutePath() + "/";
    }

    @Test
    void testCheckForCachedImageReturnName_ImageFound() throws IOException {
        // Arrange
        String fileHash = "testHash";
        String expectedName = "testImage.jpg";

        CachedImage cachedImage = new CachedImage();
        cachedImage.setHash(fileHash);
        cachedImage.setName(expectedName);

        List<CachedImage> cachedImages = new ArrayList<>();
        cachedImages.add(cachedImage);

        ObjectMapper objectMapper = new ObjectMapper();
        File cachedFile = new File(dirCachedImages);
        if (!cachedFile.exists()) {
            cachedFile.getParentFile().mkdirs();
            cachedFile.createNewFile();
        }
        objectMapper.writeValue(cachedFile, cachedImages);

        // Act
        String result = imageCachingService.checkForCachedImageReturnName(fileHash);

        // Assert
        assertEquals(expectedName, result);
    }


    @Test
    void testCheckForCachedImageReturnName_ImageNotFound() throws IOException {
        // Arrange
        String fileHash = "nonExistentHash";
        List<CachedImage> cachedImages = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();
        File cachedFile = new File(dirCachedImages);
        if (!cachedFile.exists()) {
            cachedFile.getParentFile().mkdirs();
            cachedFile.createNewFile();
        }
        objectMapper.writeValue(cachedFile, cachedImages);

        // Act
        String result = imageCachingService.checkForCachedImageReturnName(fileHash);

        // Assert
        assertNull(result);
    }


    @Test
    void testCacheImage_Success() throws IOException {
        // Arrange
        String fileHash = "testHash";
        String fileName = "testImage.jpg";
        ObjectMapper objectMapper = new ObjectMapper();
        List<CachedImage> cachedImages = new ArrayList<>();
        File cachedFile = new File(dirCachedImages);
        if (!cachedFile.exists()) {
            cachedFile.getParentFile().mkdirs();
            cachedFile.createNewFile();
        }
        objectMapper.writeValue(cachedFile, cachedImages);

        // Act
        String result = imageCachingService.cacheImage(fileHash, fileName);

        // Assert
        assertEquals(fileHash, result);
        List<CachedImage> updatedCachedImages = objectMapper.readValue(cachedFile, new TypeReference<List<CachedImage>>() {
        });
        assertEquals(1, updatedCachedImages.size());
        assertEquals(fileName, updatedCachedImages.get(0).getName());
        assertEquals(fileHash, updatedCachedImages.get(0).getHash());
    }


}

