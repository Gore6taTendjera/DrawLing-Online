package com.example.drawling.business.interfaces.service;

import com.example.drawling.domain.model.Image;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;

public interface ImageCachingService {

    String checkForCachedImageReturnName(String fileHash);
    String cacheImage(String fileHash, String fileName);
    Image checkForSavedImage(MultipartFile file);
    Image checkForSavedImage(BufferedImage bufferedImage);

}
