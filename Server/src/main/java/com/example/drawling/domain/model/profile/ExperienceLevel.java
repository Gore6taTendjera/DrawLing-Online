package com.example.drawling.domain.model.profile;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExperienceLevel {
    private int level;
    private int xpRemaining;
    private int min;
    private int max;

    public ExperienceLevel(int level, int xpRemaining, int min, int max) {
        this.level = level;
        this.xpRemaining = xpRemaining;
        this.min = min;
        this.max = max;
    }
}
