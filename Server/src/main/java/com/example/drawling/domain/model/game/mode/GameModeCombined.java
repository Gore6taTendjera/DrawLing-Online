package com.example.drawling.domain.model.game.mode;

import com.example.drawling.domain.enums.game.GameModeState;
import com.example.drawling.domain.model.game.round.GameRound;

import java.util.List;

public class GameModeCombined extends GameMode {

    public GameModeCombined(int maxPlayers, List<GameRound> gameRounds) {
        super(maxPlayers, gameRounds, GameModeState.LOBBY);
    }

    // any other game mode can be added, those models are connected
    // with their own service implementation (GameModeCombinedServiceImpl)

    // look at GameModeNormal to see real usage

}
