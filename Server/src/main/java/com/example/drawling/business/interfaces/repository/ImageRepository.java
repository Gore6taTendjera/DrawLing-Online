package com.example.drawling.business.interfaces.repository;

import com.example.drawling.domain.model.Image;

import java.util.List;

public interface ImageRepository {
    Image save(Image image);

    Image getById(int id);
    Image getImageByName(String name);
    List<Image> getAllImages();



}
