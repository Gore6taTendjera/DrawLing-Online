package com.example.drawling.domain.model.game.round;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GameRoundNormal extends GameRound {

    public GameRoundNormal(int duration) {
        super(duration);
    }

    public GameRoundNormal(int duration, String word) {
        super(duration, word);
    }


}

