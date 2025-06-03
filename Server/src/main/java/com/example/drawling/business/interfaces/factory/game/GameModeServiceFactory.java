package com.example.drawling.business.interfaces.factory.game;

import com.example.drawling.business.interfaces.service.game.GameModeService;
import com.example.drawling.domain.model.game.mode.GameMode;


public interface GameModeServiceFactory {
    <T extends GameModeService<?>> T createGameModeService(Class<T> gameModeServiceClass);

    GameModeService<?> createGameModeService(GameMode gameMode);
}