package com.example.drawling.business.interfaces.service;

import com.example.drawling.domain.model.Image;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.util.List;

public interface ImageService {

    Image saveImage(MultipartFile file);
    Image saveImage(BufferedImage bufferedImage);

    Image getById(int id);
    List<Image> getAllImages();


}