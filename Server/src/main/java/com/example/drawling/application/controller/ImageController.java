package com.example.drawling.application.controller;

import com.example.drawling.business.interfaces.service.ImageService;
import com.example.drawling.domain.dto.ImageDTO;
import com.example.drawling.domain.model.Image;
import com.example.drawling.mapper.ImageMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static com.example.drawling.constants.MyConstants.API_IMAGES_URL;

@RestController
@RequestMapping("/api/images")
public class ImageController {

    private final ImageService imageService;
    private final ImageMapper imageMapper;

    public ImageController(ImageService imageService, ImageMapper imageMapper) {
        this.imageService = imageService;
        this.imageMapper = imageMapper;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("File is empty.");
        }
        try {
            Image savedImage = imageService.saveImage(file);
            String imageUrl = API_IMAGES_URL + savedImage.getId();
            return ResponseEntity.status(HttpStatus.OK).body(imageUrl);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid file format.");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<byte[]> getImageById(@PathVariable("id") int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("ID must be greater than zero.");
        }
        try {
            ImageDTO image = imageMapper.toDto(imageService.getById(id));
            String filePath = image.getPath();
            byte[] imageBytes = Files.readAllBytes(new File(filePath).toPath());

            return ResponseEntity.status(HttpStatus.OK)
                    .contentType(MediaType.valueOf(image.getType()))
                    .body(imageBytes);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }


    @GetMapping("/all")
    public ResponseEntity<List<String>> getAllImages() {
        List<Image> images = imageService.getAllImages();

        if (images.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        List<String> imageUrls = new ArrayList<>();
        for (Image image : images) {
            String imageUrl = API_IMAGES_URL + image.getId();
            imageUrls.add(imageUrl);
        }

        return ResponseEntity.ok(imageUrls);

    }


}
