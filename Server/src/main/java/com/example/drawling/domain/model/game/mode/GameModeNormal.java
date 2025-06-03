package com.example.drawling.domain.model.game.mode;

import com.example.drawling.domain.enums.game.GameModeState;
import com.example.drawling.domain.model.game.round.GameRound;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
@NoArgsConstructor
public class GameModeNormal extends GameMode {

    public GameModeNormal(int maxPlayers, List<GameRound> gameRounds) {
        super(maxPlayers, gameRounds, GameModeState.LOBBY, 1, 1, 1, 99);
    }
}