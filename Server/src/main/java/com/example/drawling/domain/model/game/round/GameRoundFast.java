package com.example.drawling.domain.model.game.round;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class GameRoundFast extends GameRound {

    public GameRoundFast(int duration) {
        super(duration);
    }

    public GameRoundFast(int duration, String word) {
        super(duration, word);
    }


}
