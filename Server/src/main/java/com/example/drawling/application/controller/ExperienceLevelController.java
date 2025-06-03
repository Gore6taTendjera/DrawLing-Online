package com.example.drawling.application.controller;

import com.example.drawling.business.interfaces.service.ExperienceLevelService;
import com.example.drawling.domain.dto.ExperienceLevelDTO;
import com.example.drawling.mapper.ExperienceLevelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/experience-level")
public class ExperienceLevelController {

    private final ExperienceLevelService experienceLevelService;
    private final ExperienceLevelMapper experienceLevelMapper;

    public ExperienceLevelController(ExperienceLevelService experienceLevelService, ExperienceLevelMapper experienceLevelMapper) {
        this.experienceLevelService = experienceLevelService;
        this.experienceLevelMapper = experienceLevelMapper;
    }

    @PreAuthorize("authentication.principal.userId == #userId")
    @GetMapping("/{userId}/total")
    public ResponseEntity<Integer> getTotalExperience(@PathVariable int userId) {
        int experienceLevel = experienceLevelService.getTotalExperienceByUserId(userId);
        return ResponseEntity.ok(experienceLevel);
    }

    @PreAuthorize("authentication.principal.userId == #userId")
    @GetMapping("/{userId}/xp-lvl")
    public ResponseEntity<ExperienceLevelDTO> getExperienceLevel(@PathVariable int userId){
        ExperienceLevelDTO xplvl = experienceLevelMapper.toDto(experienceLevelService.getExperienceLevelByUserId(userId));
        return ResponseEntity.ok(xplvl);
    }

    @PreAuthorize("authentication.principal.userId == #userId")
    @PatchMapping("/{userId}/set")
    public ResponseEntity<String> setExperienceLevel(@PathVariable int userId, @RequestParam int amount) {
        int rowsUpdated = experienceLevelService.setExperienceLevelByUserId(userId, amount);
        if (rowsUpdated > 0) {
            return ResponseEntity.ok("Experience level updated successfully");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PreAuthorize("authentication.principal.userId == #userId")
    @PatchMapping("/{userId}/add")
    public ResponseEntity<String> addExperienceLevel(@PathVariable int userId, @RequestParam int amount) {
        int rowsUpdated = experienceLevelService.addExperienceLevelByUserId(userId, amount);
        if (rowsUpdated > 0) {
            return ResponseEntity.ok("Experience level updated successfully");
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
