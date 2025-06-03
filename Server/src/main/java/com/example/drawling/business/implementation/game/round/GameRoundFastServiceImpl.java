package com.example.drawling.business.implementation.game.round;

import com.example.drawling.business.interfaces.service.game.GameRoundService;
import com.example.drawling.domain.model.game.round.GameRound;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class GameRoundFastServiceImpl implements GameRoundService {
    private static final Logger logger = LoggerFactory.getLogger(GameRoundFastServiceImpl.class);

    public GameRoundFastServiceImpl() {
        // created by factory
    }

    @Override
    public void startRound(GameRound gameRound) {
        logger.info("Fast round started, but no active state is maintained.");
        gameRound.setActive(true);
    }

    @Override
    public void endRound(GameRound gameRound) {
        logger.info("Fast round ended, but no active state is maintained.");
        gameRound.setActive(false);
    }

    @Override
    public boolean isRoundActive(GameRound gameRound) {
        logger.warn("Checking active state for fast round, which is always inactive.");
        return gameRound.isActive();
    }
}
