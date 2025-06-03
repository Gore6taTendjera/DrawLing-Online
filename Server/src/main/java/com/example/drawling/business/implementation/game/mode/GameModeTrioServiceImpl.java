package com.example.drawling.business.implementation.game.mode;

import com.example.drawling.business.interfaces.factory.game.GameRoundServiceFactory;
import com.example.drawling.business.interfaces.service.game.GameModeService;
import com.example.drawling.domain.model.game.mode.GameModeTrio;
import com.example.drawling.domain.model.game.round.GameRound;

public class GameModeTrioServiceImpl implements GameModeService<GameModeTrio> {
    public GameModeTrioServiceImpl(GameRoundServiceFactory gameRoundServiceFactory) {
    }

    public GameRound getActiveRound(GameModeTrio gameMode) {
        for (GameRound round : gameMode.getGameRounds()) {
            if (round.isActive()) {
                return round;
            }
        }
        return null;
    }

}
