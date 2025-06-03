package com.example.drawling.business;

import com.example.drawling.business.implementation.ExperienceLevelServiceImpl;
import com.example.drawling.business.interfaces.repository.ExperienceLevelRepository;
import com.example.drawling.domain.model.profile.ExperienceLevel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExperienceLevelServiceImplTest {

    @Mock
    private ExperienceLevelRepository experienceLevelRepository;

    @InjectMocks
    private ExperienceLevelServiceImpl experienceLevelService;

    @Test
    void getTotalExperience_ShouldReturnTotalExperience_ByUserId_WhenUserIdIsValid() {
        // Arrange
        int userId = 1;
        int expectedExperience = 100;

        when(experienceLevelRepository.getTotalExperience(userId)).thenReturn(expectedExperience);

        // Act
        int totalExperience = experienceLevelService.getTotalExperienceByUserId(userId);

        // Assert
        assertEquals(expectedExperience, totalExperience);
        verify(experienceLevelRepository, times(1)).getTotalExperience(userId);
    }

    @Test
    void setExperienceLevel_ByUserId_ShouldThrowException_WhenUserIdIsInvalid() {
        // Arrange
        int userId = -1;
        double amount = 50.0;

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                experienceLevelService.setExperienceLevelByUserId(userId, amount));
        assertEquals("User id must be greater than zero.", exception.getMessage());
        verify(experienceLevelRepository, never()).setExperienceLevelByUserId(anyInt(), anyDouble());
    }

    @Test
    void setExperienceLevel_ByUserId_ShouldCallRepository_WhenUserIdIsValid() {
        // Arrange
        int userId = 1;
        double amount = 50.0;

        // Act
        experienceLevelService.setExperienceLevelByUserId(userId, amount);

        // Assert
        verify(experienceLevelRepository, times(1)).setExperienceLevelByUserId(userId, amount);
    }

    @Test
    void testGetExperienceLevelByUserId_ValidUser() {
        int userId = 1;
        int userXp = 1500;
        when(experienceLevelRepository.getTotalExperience(userId)).thenReturn(userXp);

        ExperienceLevel experienceLevel = experienceLevelService.getExperienceLevelByUserId(userId);

        assertNotNull(experienceLevel);
        assertEquals(24, experienceLevel.getLevel());
        assertEquals(100, experienceLevel.getXpRemaining());
        assertEquals(1500, experienceLevel.getMin());
        assertEquals(1600, experienceLevel.getMax());
    }

    @Test
    void testGetExperienceLevelByUserId_InvalidUserId() {
        int userId = -1;
        assertThrows(IllegalArgumentException.class, () -> experienceLevelService.getExperienceLevelByUserId(userId));
    }

    @Test
    void testGetUserLevel_XpExceedsMaxLevel() {
        int userXp = 250000;
        when(experienceLevelRepository.getTotalExperience(anyInt())).thenReturn(userXp);
        int userLevel = experienceLevelService.getTotalExperienceByUserId(userXp);
        assertEquals(250000, userLevel);
    }

    @Test
    void testGetXpForLevel_LevelZero() {
        int level = 0;

        int xp = experienceLevelService.getXpForLevel(level);

        assertEquals(0, xp);
    }

    @Test
    void testGetUserLevel_XpInLowLevelRange() {
        int userXp = 50;
        when(experienceLevelRepository.getTotalExperience(anyInt())).thenReturn(userXp);
        int userLevel = experienceLevelService.getTotalExperienceByUserId(userXp);
        assertEquals(50, userLevel);
    }

    @Test
    void testGetUserLevel_NoValidLevel() {
        int userXp = -50; // Invalid XP
        when(experienceLevelRepository.getTotalExperience(anyInt())).thenReturn(userXp);
        int userLevel = experienceLevelService.getTotalExperienceByUserId(userXp);
        assertEquals(-50, userLevel);
    }


    @Test
    void testGetUserLevel_XpNo() {
        int userXp = 0;
        when(experienceLevelRepository.getTotalExperience(anyInt())).thenReturn(userXp);
        int userLevel = experienceLevelService.getTotalExperienceByUserId(userXp);
        assertEquals(0, userLevel);
    }


    @Test
    void addExperienceLevel_ByUserId_ShouldThrowException_WhenUserIdIsInvalid() {
        // Arrange
        int userId = -1;
        double amount = 50.0;

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                experienceLevelService.addExperienceLevelByUserId(userId, amount));
        assertEquals("User id must be greater than zero.", exception.getMessage());
        verify(experienceLevelRepository, never()).addExperienceLevelByUserId(anyInt(), anyDouble());
    }

    @Test
    void addExperienceLevel_ByUserId_ShouldThrowException_WhenAmountIsNonPositive() {
        // Arrange
        int userId = 1;
        double amount = -10.0;

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                experienceLevelService.addExperienceLevelByUserId(userId, amount));
        assertEquals("Experience level amount must be greater than zero.", exception.getMessage());
        verify(experienceLevelRepository, never()).addExperienceLevelByUserId(anyInt(), anyDouble());
    }

    @Test
    void addExperienceLevel_ByUserId_ShouldCallRepository_WhenInputsAreValid() {
        // Arrange
        int userId = 1;
        double amount = 50.0;

        // Act
        experienceLevelService.addExperienceLevelByUserId(userId, amount);

        // Assert
        verify(experienceLevelRepository, times(1)).addExperienceLevelByUserId(userId, amount);
    }

    @Test
    void getExperienceLevel_ByUserId_ShouldThrowException_WhenUserIdIsInvalid() {
        // Arrange
        int userId = -1;

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                experienceLevelService.getExperienceLevelByUserId(userId));
        assertEquals("User id must be greater than zero.", exception.getMessage());
        verify(experienceLevelRepository, never()).getTotalExperience(anyInt());
    }


    @Test
    void testGetExperienceLevelByUserId_ValidUserId() {
        int userId = 1;
        int userXp = 150; // User XP between level 1 and 2

        when(experienceLevelRepository.getTotalExperience(userId)).thenReturn(userXp);

        ExperienceLevel experienceLevel = experienceLevelService.getExperienceLevelByUserId(userId);

        assertEquals(10, experienceLevel.getLevel()); // Expected level
        assertEquals(50, experienceLevel.getXpRemaining()); // XP to next level
        assertEquals(100, experienceLevel.getMin()); // XP at current level
        assertEquals(200, experienceLevel.getMax()); // XP at next level

        verify(experienceLevelRepository).getTotalExperience(userId);
    }

    @Test
    void testGetUserLevel_MaxLevel() {
        int maxLevelXp = experienceLevelService.getXpForLevel(1000);

        int userLevel = experienceLevelService.getUserLevel(maxLevelXp);

        assertEquals(1000, userLevel); // User is at max level
    }

    @Test
    void testGetUserLevel_BeyondMaxLevel() {
        int beyondMaxXp = experienceLevelService.getXpForLevel(1000) + 500;

        int userLevel = experienceLevelService.getUserLevel(beyondMaxXp);

        assertEquals(1000, userLevel); // User is capped at max level
    }

    @Test
    void testGetExperienceLevelByUserId_InvalidUserId_ThrowsException() {
        int invalidUserId = -1;

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> experienceLevelService.getExperienceLevelByUserId(invalidUserId)
        );

        assertEquals("User id must be greater than zero.", exception.getMessage());
    }

    @Test
    void testGetUserLevel_IntermediateLevel() {
        int level9Xp = experienceLevelService.getXpForLevel(9);

        int userLevel = experienceLevelService.getUserLevel(level9Xp);

        assertEquals(9, userLevel);
    }


    @Test
    void testAddExperienceLevelByUserId_Valid() {
        int userId = 1;
        double amount = 100.0;

        when(experienceLevelRepository.addExperienceLevelByUserId(userId, amount)).thenReturn(1);

        int result = experienceLevelService.addExperienceLevelByUserId(userId, amount);

        assertEquals(1, result);
        verify(experienceLevelRepository).addExperienceLevelByUserId(userId, amount);
    }

    @Test
    void testAddExperienceLevelByUserId_InvalidAmount_ThrowsException() {
        int userId = 1;
        double invalidAmount = -100.0;

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> experienceLevelService.addExperienceLevelByUserId(userId, invalidAmount)
        );

        assertEquals("Experience level amount must be greater than zero.", exception.getMessage());
    }

    @Test
    void testAddExperienceLevelByUserId_InvalidUserId_ThrowsException() {
        int invalidUserId = -1;
        double amount = 100.0;

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> experienceLevelService.addExperienceLevelByUserId(invalidUserId, amount)
        );

        assertEquals("User id must be greater than zero.", exception.getMessage());
    }

    @Test
    void getUserLevel_userXpFallsOutsideExpectedRange_shouldReturnZero() {

        int xp = experienceLevelService.getXpForLevel(0);

        int userLevel = experienceLevelService.getUserLevel(xp);

        assertEquals(0, userLevel);
    }


}
