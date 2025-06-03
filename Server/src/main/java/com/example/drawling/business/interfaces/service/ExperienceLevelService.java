package com.example.drawling.business.interfaces.service;


import com.example.drawling.domain.model.profile.ExperienceLevel;

public interface ExperienceLevelService {
    ExperienceLevel getExperienceLevelByUserId(int userId);
    int getTotalExperienceByUserId(int userId);
    int setExperienceLevelByUserId(int userId, double amount);
    int addExperienceLevelByUserId(int userId, double amount);
}
