package com.example.drawling.repository.implementation;


import com.example.drawling.business.interfaces.repository.ExperienceLevelRepository;
import com.example.drawling.exception.ExperienceLevelRetrievalException;
import com.example.drawling.exception.ExperienceLevelUpdateException;
import com.example.drawling.repository.jpa.ExperienceLevelRepositoryJPA;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class ExperienceLevelRepositoryImpl implements ExperienceLevelRepository {

    private final ExperienceLevelRepositoryJPA experienceLevelRepositoryJPA;

    public ExperienceLevelRepositoryImpl(ExperienceLevelRepositoryJPA experienceLevelRepositoryJPA) {
        this.experienceLevelRepositoryJPA = experienceLevelRepositoryJPA;
    }

    @Transactional(readOnly = true)
    public int getTotalExperience(int userId) {
        try {
            return experienceLevelRepositoryJPA.getTotalExperienceByUserId(userId);
        } catch (DataAccessException e) {
            throw new ExperienceLevelRetrievalException("Error getting total experience for user ID " + userId, e);
        }
    }

    @Transactional
    public int setExperienceLevelByUserId(int userId, double newExperienceLevel) {
        try {
            return experienceLevelRepositoryJPA.setExperienceLevel(userId, newExperienceLevel);
        } catch (DataAccessException e) {
            throw new ExperienceLevelUpdateException("Error updating experience level for user ID " + userId, e);
        }
    }

    @Transactional
    public int addExperienceLevelByUserId(int userId, double amount) {
        try {
            return experienceLevelRepositoryJPA.addExperienceLevel(userId, amount);
        } catch (DataAccessException e) {
            throw new ExperienceLevelUpdateException("Error updating experience level for user ID " + userId, e);
        }
    }
}
