package com.example.drawling.business.implementation.game.mode;

import com.example.drawling.business.interfaces.factory.game.GameRoundServiceFactory;
import com.example.drawling.business.interfaces.service.game.GameModeService;
import com.example.drawling.domain.model.game.mode.GameModeNormal;
import com.example.drawling.domain.model.game.round.GameRound;
import org.springframework.stereotype.Service;

@Service
public class GameModeNormalServiceImpl implements GameModeService<GameModeNormal> {

    // used by factory
    public GameModeNormalServiceImpl(GameRoundServiceFactory gameRoundServiceFactory) {

    }

    // example method
    public GameRound getActiveRound(GameModeNormal gameMode) {
        return gameMode.getGameRounds().stream()
                .filter(GameRound::isActive)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No active round found in game mode: " + gameMode));
    }

}
