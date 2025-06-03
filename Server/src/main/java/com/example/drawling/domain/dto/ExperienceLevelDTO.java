package com.example.drawling.domain.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExperienceLevelDTO {
    private int level;
    private int xpRemaining;
    private int min;
    private int max;
}
