package com.example.drawling.business.interfaces.factory.game;

import com.example.drawling.business.interfaces.service.game.GameRoundService;
import com.example.drawling.domain.model.game.round.GameRound;

public interface GameRoundServiceFactory {
    GameRoundService createGameRoundService(GameRoundService gameRoundService);
    GameRoundService createGameRoundServiceFromModel(GameRound gameRound);
}
