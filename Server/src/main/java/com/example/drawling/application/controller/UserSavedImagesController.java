package com.example.drawling.application.controller;

import com.example.drawling.business.implementation.ImageConverterService;
import com.example.drawling.business.interfaces.service.UserSavedImageService;
import com.example.drawling.domain.model.Image;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.example.drawling.constants.MyConstants.API_IMAGES_URL;

@RestController
@RequestMapping("/api/images")
public class UserSavedImagesController {
    private final UserSavedImageService userSavedImageService;
    private final ImageConverterService imageConverterService;


    public UserSavedImagesController(UserSavedImageService userSavedImageService, ImageConverterService imageConverterService) {
        this.userSavedImageService = userSavedImageService;
        this.imageConverterService = imageConverterService;
    }


    @PreAuthorize("authentication.principal.userId == #userId")
    @GetMapping("/user/{userId}/saved-drawings")
    public ResponseEntity<List<String>> getUserSavedDrawings(@PathVariable("userId") int userId) {
        List<Image> savedDrawings = userSavedImageService.getUserSavedDrawings(userId);

        List<String> imageUrls = new ArrayList<>();
        for (Image image : savedDrawings) {
            String imageUrl = API_IMAGES_URL + image.getId();
            imageUrls.add(imageUrl);
        }

        return ResponseEntity.ok(imageUrls);

    }


    @PreAuthorize("authentication.principal.userId == #userId")
    @PostMapping("/user/{userId}/saved-drawings")
    public ResponseEntity<String> saveUserDrawing(@PathVariable int userId, @RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("File is empty.");
        }

        if (userId <= 0) {
            throw new IllegalArgumentException("ID must be greater than zero.");
        }

        try {
            imageConverterService.convert(file);

            int image = userSavedImageService.saveUserDrawing(userId, file);

            String imageUrl = API_IMAGES_URL + image;
            return ResponseEntity.status(HttpStatus.OK).body(imageUrl);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

}
