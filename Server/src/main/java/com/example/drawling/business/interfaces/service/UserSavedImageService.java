package com.example.drawling.business.interfaces.service;

import com.example.drawling.domain.model.Image;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.util.List;

public interface UserSavedImageService {
    int saveUserDrawing(int userId, MultipartFile file);
    int saveUserDrawing(int userId, BufferedImage image);
    List<Image> getUserSavedDrawings(int userId);
}
