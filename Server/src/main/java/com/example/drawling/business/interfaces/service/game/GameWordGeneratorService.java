package com.example.drawling.business.interfaces.service.game;

import com.example.drawling.domain.enums.game.GameWordCategoryEnum;

public interface GameWordGeneratorService {
    String generateRandomWordFromCategory(GameWordCategoryEnum gameWordCategoryEnum);
    String generateRandomWordFromRandomCategory();
}
