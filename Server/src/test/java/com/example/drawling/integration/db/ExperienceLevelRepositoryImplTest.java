package com.example.drawling.integration.db;

import com.example.drawling.exception.ExperienceLevelRetrievalException;
import com.example.drawling.exception.ExperienceLevelUpdateException;
import com.example.drawling.repository.implementation.ExperienceLevelRepositoryImpl;
import com.example.drawling.repository.jpa.ExperienceLevelRepositoryJPA;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataAccessException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class ExperienceLevelRepositoryImplTest {

    @Mock
    private ExperienceLevelRepositoryJPA experienceLevelRepositoryJPA;

    @InjectMocks
    private ExperienceLevelRepositoryImpl experienceLevelRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetTotalExperience_Success() {
        int userId = 1;
        int expectedExperience = 5;

        when(experienceLevelRepositoryJPA.getTotalExperienceByUserId(userId)).thenReturn(expectedExperience);

        int actualExperience = experienceLevelRepository.getTotalExperience(userId);

        assertEquals(expectedExperience, actualExperience);
        verify(experienceLevelRepositoryJPA, times(1)).getTotalExperienceByUserId(userId);
    }

    @Test
    void testGetTotalExperience_DataAccessException() {
        int userId = 1;

        when(experienceLevelRepositoryJPA.getTotalExperienceByUserId(userId)).thenThrow(new DataAccessException("Database error") {});

        assertThrows(ExperienceLevelRetrievalException.class, () -> {
            experienceLevelRepository.getTotalExperience(userId);
        });
    }

    @Test
    void testSetExperienceLevelByUserId_Success() {
        int userId = 1;
        double newExperienceLevel = 10.0;
        int rowsUpdated = 1;

        when(experienceLevelRepositoryJPA.setExperienceLevel(userId, newExperienceLevel)).thenReturn(rowsUpdated);

        int actualRowsUpdated = experienceLevelRepository.setExperienceLevelByUserId(userId, newExperienceLevel);

        assertEquals(rowsUpdated, actualRowsUpdated);
        verify(experienceLevelRepositoryJPA, times(1)).setExperienceLevel(userId, newExperienceLevel);
    }

    @Test
    void testSetExperienceLevelByUserId_DataAccessException() {
        int userId = 1;
        double newExperienceLevel = 10.0;

        when(experienceLevelRepositoryJPA.setExperienceLevel(userId, newExperienceLevel)).thenThrow(new DataAccessException("Database error") {});

        assertThrows(ExperienceLevelUpdateException.class, () -> {
            experienceLevelRepository.setExperienceLevelByUserId(userId, newExperienceLevel);
        });
    }

    @Test
    void testAddExperienceLevelByUserId_Success() {
        int userId = 1;
        double amount = 5.0;
        int rowsUpdated = 1;

        when(experienceLevelRepositoryJPA.addExperienceLevel(userId, amount)).thenReturn(rowsUpdated);

        int actualRowsUpdated = experienceLevelRepository.addExperienceLevelByUserId(userId, amount);

        assertEquals(rowsUpdated, actualRowsUpdated);
        verify(experienceLevelRepositoryJPA, times(1)).addExperienceLevel(userId, amount);
    }

    @Test
    void testAddExperienceLevelByUserId_DataAccessException() {
        int userId = 1;
        double amount = 5.0;

        when(experienceLevelRepositoryJPA.addExperienceLevel(userId, amount)).thenThrow(new DataAccessException("Database error") {});

        assertThrows(ExperienceLevelUpdateException.class, () -> {
            experienceLevelRepository.addExperienceLevelByUserId(userId, amount);
        });
    }
}