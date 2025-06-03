package com.example.drawling.repository.implementation;

import com.example.drawling.business.interfaces.repository.ImageRepository;
import com.example.drawling.domain.entity.ImageEntity;
import com.example.drawling.domain.model.Image;
import com.example.drawling.exception.ImageNotFoundException;
import com.example.drawling.exception.ImageSaveException;
import com.example.drawling.exception.ImageValidationException;
import com.example.drawling.mapper.ImageMapper;
import com.example.drawling.repository.jpa.ImageRepositoryJPA;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class ImageRepositoryImpl implements ImageRepository {

    private final ImageRepositoryJPA imageRepositoryJPA;
    private final ImageMapper imageMapper;

    public ImageRepositoryImpl(ImageRepositoryJPA imageRepository, ImageMapper imageMapper) {
        this.imageRepositoryJPA = imageRepository;
        this.imageMapper = imageMapper;
    }

    @Transactional
    public Image save(Image image) {
        if (image == null) {
            throw new ImageValidationException("Image cannot be null.");
        }

        validateImage(image);

        try {
            ImageEntity savedImageEntity = imageRepositoryJPA.save(imageMapper.toEntity(image));
            return imageRepositoryJPA.findById(savedImageEntity.getId())
                    .map(imageMapper::toModel)
                    .orElseThrow(() -> new ImageNotFoundException("Image not found after saving with id: " + savedImageEntity.getId()));
        } catch (DataAccessException e) {
            throw new ImageSaveException("Failed to save the image due to a data access issue: " + e.getMessage(), e);
        }
    }

    @Transactional(readOnly = true)
    public Image getById(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("ID must be greater than zero.");
        }

        try {
            return imageRepositoryJPA.findById(id)
                    .map(imageMapper::toModel)
                    .orElseThrow(() -> new ImageNotFoundException("Image not found with id: " + id));
        } catch (DataAccessException e) {
            throw new ImageNotFoundException("Error retrieving image with id: " + id + " due to a data access issue.", e);
        }
    }

    @Transactional(readOnly = true)
    public Image getImageByName(String name) {
        try {
            return imageRepositoryJPA.findByName(name)
                    .map(imageMapper::toModel)
                    .orElseThrow(() -> new ImageNotFoundException("Image not found with name: " + name));
        } catch (DataAccessException e) {
            throw new ImageNotFoundException("Error retrieving image with name: " + name + " due to a data access issue.", e);
        }
    }

    @Transactional(readOnly = true)
    public List<Image> getAllImages() {
        try {
            List<ImageEntity> imageEntities = (List<ImageEntity>) imageRepositoryJPA.findAll();
            if (imageEntities.isEmpty()) {
                throw new ImageNotFoundException("No images found.");
            }
            return imageEntities.stream()
                    .map(imageMapper::toModel)
                    .toList();
        } catch (DataAccessException e) {
            throw new ImageNotFoundException("Error retrieving all images due to a data access issue.", e);
        }
    }

    private void validateImage(Image image) {
        if (image.getName() == null || image.getName().isEmpty()) {
            throw new ImageValidationException("Image name cannot be null or empty.");
        }

        if (image.getPath() == null || image.getPath().isEmpty()) {
            throw new ImageValidationException("Image path cannot be null or empty.");
        }
    }
}