package com.example.drawling.business.interfaces.repository;

import com.example.drawling.domain.model.Image;
import com.example.drawling.domain.model.SavedImage;

import java.util.List;

public interface UserSavedImageRepository {
    SavedImage addUserSavedImage(int userId, int imageId);
    List<Image> getUserSavedImagesByUserId(int userId);
}
