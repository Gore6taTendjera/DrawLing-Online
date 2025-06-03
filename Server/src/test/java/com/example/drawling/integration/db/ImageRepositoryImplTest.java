package com.example.drawling.integration.db;

import com.example.drawling.domain.entity.ImageEntity;
import com.example.drawling.domain.model.Image;
import com.example.drawling.exception.ImageNotFoundException;
import com.example.drawling.exception.ImageSaveException;
import com.example.drawling.exception.ImageValidationException;
import com.example.drawling.mapper.ImageMapper;
import com.example.drawling.repository.implementation.ImageRepositoryImpl;
import com.example.drawling.repository.jpa.ImageRepositoryJPA;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataAccessException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ImageRepositoryImplTest {

    @Mock
    private ImageRepositoryJPA imageRepositoryJPA;

    @Mock
    private ImageMapper imageMapper;

    @InjectMocks
    private ImageRepositoryImpl imageRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSave_Success() {
        Image image = new Image();
        image.setName("testImage");
        image.setPath("/path/to/image");

        ImageEntity imageEntity = new ImageEntity();
        imageEntity.setId(1);

        when(imageMapper.toEntity(image)).thenReturn(imageEntity);
        when(imageRepositoryJPA.save(imageEntity)).thenReturn(imageEntity);
        when(imageRepositoryJPA.findById(1)).thenReturn(Optional.of(imageEntity));
        when(imageMapper.toModel(imageEntity)).thenReturn(image);

        Image savedImage = imageRepository.save(image);

        assertNotNull(savedImage);
        assertEquals(image.getName(), savedImage.getName());
        verify(imageRepositoryJPA, times(1)).save(imageEntity);
    }

    @Test
    void testSave_ImageValidationException() {
        assertThrows(ImageValidationException.class, () -> {
            imageRepository.save(null);
        });
    }

    @Test
    void testSave_DataAccessException() {
        Image image = new Image();
        image.setName("testImage");
        image.setPath("/path/to/image");

        ImageEntity imageEntity = new ImageEntity();

        when(imageMapper.toEntity(image)).thenReturn(imageEntity);
        when(imageRepositoryJPA.save(imageEntity)).thenThrow(new DataAccessException("Database error") {});

        assertThrows(ImageSaveException.class, () -> {
            imageRepository.save(image);
        });
    }

    @Test
    void testGetById_Success() {
        int id = 1;
        ImageEntity imageEntity = new ImageEntity();
        Image image = new Image();
        image.setName("testImage");

        when(imageRepositoryJPA.findById(id)).thenReturn(Optional.of(imageEntity));
        when(imageMapper.toModel(imageEntity)).thenReturn(image);

        Image foundImage = imageRepository.getById(id);

        assertNotNull(foundImage);
        assertEquals(image.getName(), foundImage.getName());
        verify(imageRepositoryJPA, times(1)).findById(id);
    }

    @Test
    void testGetById_ImageNotFoundException() {
        int id = 1;

        when(imageRepositoryJPA.findById(id)).thenReturn(Optional.empty());

        assertThrows(ImageNotFoundException.class, () -> {
            imageRepository.getById(id);
        });
    }

    @Test
    void testGetById_InvalidId() {
        assertThrows(IllegalArgumentException.class, () -> {
            imageRepository.getById(0);
        });
    }

    @Test
    void testGetImageByName_Success() {
        String name = "testImage";
        ImageEntity imageEntity = new ImageEntity();
        Image image = new Image();
        image.setName(name);

        when(imageRepositoryJPA.findByName(name)).thenReturn(Optional.of(imageEntity));
        when(imageMapper.toModel(imageEntity)).thenReturn(image);

        Image foundImage = imageRepository.getImageByName(name);

        assertNotNull(foundImage);
        assertEquals(image.getName(), foundImage.getName());
        verify(imageRepositoryJPA, times(1)).findByName(name);
    }

    @Test
    void testGetImageByName_ImageNotFoundException() {
        String name = "testImage";

        when(imageRepositoryJPA.findByName(name)).thenReturn(Optional.empty());

        assertThrows(ImageNotFoundException.class, () -> {
            imageRepository.getImageByName(name);
        });
    }
    @Test
    void testGetAllImages_Success() {
        ImageEntity imageEntity1 = new ImageEntity();
        imageEntity1.setId(1);
        ImageEntity imageEntity2 = new ImageEntity();
        imageEntity2.setId(2);

        List<ImageEntity> imageEntities = List.of(imageEntity1, imageEntity2);
        Image image1 = new Image();
        Image image2 = new Image();

        when(imageRepositoryJPA.findAll()).thenReturn(imageEntities);
        when(imageMapper.toModel(imageEntity1)).thenReturn(image1);
        when(imageMapper.toModel(imageEntity2)).thenReturn(image2);

        List<Image> images = imageRepository.getAllImages();

        assertNotNull(images);
        assertEquals(2, images.size());
        verify(imageRepositoryJPA, times(1)).findAll();
    }

    @Test
    void testGetAllImages_NoImagesFound() {
        when(imageRepositoryJPA.findAll()).thenReturn(List.of());

        assertThrows(ImageNotFoundException.class, () -> {
            imageRepository.getAllImages();
        });
    }

    @Test
    void testGetAllImages_DataAccessException() {
        when(imageRepositoryJPA.findAll()).thenThrow(new DataAccessException("Database error") {});

        assertThrows(ImageNotFoundException.class, () -> {
            imageRepository.getAllImages();
        });
    }
}
