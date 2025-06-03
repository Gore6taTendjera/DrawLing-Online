package com.example.drawling.business.factory.game;

import com.example.drawling.business.implementation.game.round.GameRoundFastServiceImpl;
import com.example.drawling.business.implementation.game.round.GameRoundNormalServiceImpl;
import com.example.drawling.business.interfaces.factory.game.GameRoundServiceFactory;
import com.example.drawling.business.interfaces.service.game.GameRoundService;
import com.example.drawling.domain.model.game.round.GameRound;
import com.example.drawling.domain.model.game.round.GameRoundFast;
import com.example.drawling.domain.model.game.round.GameRoundNormal;
import org.springframework.stereotype.Service;

/**
 * Each GameRoundService can have its own services inside.
 */
@Service
public class GameRoundServiceFactoryImpl implements GameRoundServiceFactory {

    public GameRoundService createGameRoundService(GameRoundService gameRoundService) {
        return switch (gameRoundService) {
            case GameRoundNormalServiceImpl gameRoundNormalServiceImpl -> new GameRoundNormalServiceImpl();
            case GameRoundFastServiceImpl gameRoundFastServiceImpl -> new GameRoundFastServiceImpl();
            default -> throw new IllegalArgumentException("Unknown game round service: " + gameRoundService.getClass().getName());
        };
    }

    public GameRoundService createGameRoundServiceFromModel(GameRound gameRound) {
        return switch (gameRound) {
            case GameRoundNormal gameRoundNormal -> new GameRoundNormalServiceImpl();
            case GameRoundFast gameRoundFast -> new GameRoundFastServiceImpl();
            default -> throw new IllegalArgumentException("Unknown game round service: " + gameRound.getClass().getName());
        };
    }

}