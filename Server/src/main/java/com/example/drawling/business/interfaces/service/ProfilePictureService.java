package com.example.drawling.business.interfaces.service;

import com.example.drawling.domain.model.Image;
import org.springframework.web.multipart.MultipartFile;

public interface ProfilePictureService {
    Image saveUserProfilePicture(int userId, MultipartFile file);
    Image getUserProfilePictureById(int userId);
    int getUserProfilePictureIdByUserId(int userId);
}
