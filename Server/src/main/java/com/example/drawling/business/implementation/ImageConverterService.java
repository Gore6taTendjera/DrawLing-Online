package com.example.drawling.business.implementation;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

@Service
public class ImageConverterService {
    public BufferedImage convert(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        // Convert MultipartFile to BufferedImage
        return ImageIO.read(file.getInputStream());
    }
}
