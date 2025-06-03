package com.example.drawling.business.interfaces.repository;

public interface ExperienceLevelRepository {

    int getTotalExperience(int userId);

    int setExperienceLevelByUserId(int userId, double newExperienceLevel);

    int addExperienceLevelByUserId(int userId, double amount);
}
