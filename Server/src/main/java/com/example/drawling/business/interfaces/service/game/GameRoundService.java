package com.example.drawling.business.interfaces.service.game;

import com.example.drawling.domain.model.game.round.GameRound;

public interface GameRoundService {
    void startRound(GameRound gameRound);
    void endRound(GameRound gameRound);
    boolean isRoundActive(GameRound gameRound);
}
