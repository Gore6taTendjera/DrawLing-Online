package com.example.drawling.domain.dto.game;

import com.example.drawling.domain.enums.game.GameRoundEnum;
import com.example.drawling.domain.enums.game.GameWordCategoryEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GameRoundRequestDTO {
    private GameRoundEnum gameRoundEnum;
    private int duration;
    private GameWordCategoryEnum wordCategory;
}
