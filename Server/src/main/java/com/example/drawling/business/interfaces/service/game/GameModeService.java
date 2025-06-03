package com.example.drawling.business.interfaces.service.game;

import com.example.drawling.domain.model.game.mode.GameMode;
import com.example.drawling.domain.model.game.round.GameRound;

// each service class will have a specific model that they have to use
// this way you can create a new service class for each model
// and at the same time not allow a different model to be used
public interface GameModeService<T extends GameMode> {

    // when creating a new service class, the compiler will not allow you
    // to place different model in method argument where the T is located

    GameRound getActiveRound(T gameMode);
}
