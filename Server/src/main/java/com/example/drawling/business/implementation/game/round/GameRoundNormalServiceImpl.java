package com.example.drawling.business.implementation.game.round;

import com.example.drawling.business.interfaces.service.game.GameRoundService;
import com.example.drawling.domain.model.game.round.GameRound;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class GameRoundNormalServiceImpl implements GameRoundService {
    private static final Logger logger = LoggerFactory.getLogger(GameRoundNormalServiceImpl.class);

    public GameRoundNormalServiceImpl() {
        // created by factory
    }

    public void startRound(GameRound gameRound) {
        gameRound.setActive(true);
        logger.info("Round started with duration: {} seconds.", gameRound.getDuration());
    }

    public void endRound(GameRound gameRound) {
        gameRound.setActive(false);
        logger.info("Round ended.");
    }

    public boolean isRoundActive(GameRound gameRound) {
        return gameRound.isActive();
    }
}