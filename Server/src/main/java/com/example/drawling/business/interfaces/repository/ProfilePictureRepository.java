package com.example.drawling.business.interfaces.repository;

import com.example.drawling.domain.model.Image;

public interface ProfilePictureRepository {
    void setUserProfilePicture(int userId, int imageId);
    Image getUserProfilePictureById(int userId);
}
