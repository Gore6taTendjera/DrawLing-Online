package com.example.drawling.integration.api;

import com.example.drawling.application.controller.ExperienceLevelController;
import com.example.drawling.business.interfaces.service.ExperienceLevelService;
import com.example.drawling.domain.dto.ExperienceLevelDTO;
import com.example.drawling.domain.model.profile.ExperienceLevel;
import com.example.drawling.mapper.ExperienceLevelMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ExperienceLevelControllerTest {

    @Mock
    private ExperienceLevelService experienceLevelService;

    @Mock
    private ExperienceLevelMapper experienceLevelMapper;

    @InjectMocks
    private ExperienceLevelController experienceLevelController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetTotalExperience() {
        int userId = 1;
        int expectedExperience = 100;

        when(experienceLevelService.getTotalExperienceByUserId(userId)).thenReturn(expectedExperience);

        ResponseEntity<Integer> response = experienceLevelController.getTotalExperience(userId);

        assertEquals(ResponseEntity.ok(expectedExperience), response);
        verify(experienceLevelService, times(1)).getTotalExperienceByUserId(userId);
    }

    @Test
    void testGetExperienceLevel() {
        int userId = 1;
        ExperienceLevel experienceLevel = new ExperienceLevel(2, 50, 0, 100);
        ExperienceLevelDTO expectedDto = new ExperienceLevelDTO();

        when(experienceLevelService.getExperienceLevelByUserId(userId)).thenReturn(experienceLevel);
        when(experienceLevelMapper.toDto(experienceLevel)).thenReturn(expectedDto);

        ResponseEntity<ExperienceLevelDTO> response = experienceLevelController.getExperienceLevel(userId);

        assertEquals(ResponseEntity.ok(expectedDto), response);
        verify(experienceLevelService, times(1)).getExperienceLevelByUserId(userId);
        verify(experienceLevelMapper, times(1)).toDto(experienceLevel);
    }

    @Test
    void testSetExperienceLevel() {
        int userId = 1;
        int amount = 50;
        when(experienceLevelService.setExperienceLevelByUserId(userId, amount)).thenReturn(1);

        ResponseEntity<String> response = experienceLevelController.setExperienceLevel(userId, amount);

        assertEquals(ResponseEntity.ok("Experience level updated successfully"), response);
        verify(experienceLevelService, times(1)).setExperienceLevelByUserId(userId, amount);
    }

    @Test
    void testSetExperienceLevelNotFound() {
        int userId = 1;
        int amount = 50;
        when(experienceLevelService.setExperienceLevelByUserId(userId, amount)).thenReturn(0);

        ResponseEntity<String> response = experienceLevelController.setExperienceLevel(userId, amount);

        assertEquals(ResponseEntity.notFound().build(), response);
        verify(experienceLevelService, times(1)).setExperienceLevelByUserId(userId, amount);
    }

    @Test
    void testAddExperienceLevel() {
        int userId = 1;
        int amount = 50;
        when(experienceLevelService.addExperienceLevelByUserId(userId, amount)).thenReturn(1);

        ResponseEntity<String> response = experienceLevelController.addExperienceLevel(userId, amount);

        assertEquals(ResponseEntity.ok("Experience level updated successfully"), response);
        verify(experienceLevelService, times(1)).addExperienceLevelByUserId(userId, amount);
    }

    @Test
    void testAddExperienceLevelNotFound() {
        int userId = 1;
        int amount = 50;
        when(experienceLevelService.addExperienceLevelByUserId(userId, amount)).thenReturn(0);

        ResponseEntity<String> response = experienceLevelController.addExperienceLevel(userId, amount);

        assertEquals(ResponseEntity.notFound().build(), response);
        verify(experienceLevelService, times(1)).addExperienceLevelByUserId(userId, amount);
    }
}