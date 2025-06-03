package com.example.drawling.application.controller;

import com.example.drawling.business.interfaces.service.ProfilePictureService;
import com.example.drawling.domain.model.Image;
import com.example.drawling.exception.ImageNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static com.example.drawling.constants.MyConstants.API_IMAGES_URL;

@RestController
@RequestMapping("/api/images")
public class ProfilePictureController {

    private final ProfilePictureService profilePictureService;

    public ProfilePictureController(ProfilePictureService profilePictureService) {
        this.profilePictureService = profilePictureService;
    }

    @PreAuthorize("authentication.principal.userId == #userId")
    @PostMapping("/user/{userId}/profile-picture")
    public ResponseEntity<String> uploadUserProfilePicture(@PathVariable("userId") int userId, @RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("File is empty.");
        }

        try {
            Image savedImage = profilePictureService.saveUserProfilePicture(userId, file);
            String imageUrl = API_IMAGES_URL + savedImage.getId();

            return ResponseEntity.status(HttpStatus.OK)
                    .body(imageUrl);
        } catch (ImageNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);
        }
    }

    @GetMapping("/user/{userId}/profile-picture")
    public ResponseEntity<String> getUserProfilePicture(@PathVariable("userId") int userId) {
        try {
            Image image = profilePictureService.getUserProfilePictureById(userId);
            String imageUrl = API_IMAGES_URL + image.getId();

            return ResponseEntity.status(HttpStatus.OK)
                    .body(imageUrl);
        } catch (ImageNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }
}
