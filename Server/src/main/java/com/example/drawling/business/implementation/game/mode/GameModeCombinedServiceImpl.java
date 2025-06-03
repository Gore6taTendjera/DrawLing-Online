package com.example.drawling.business.implementation.game.mode;

import com.example.drawling.business.interfaces.factory.game.GameRoundServiceFactory;
import com.example.drawling.business.interfaces.service.game.GameModeService;
import com.example.drawling.domain.model.game.mode.GameModeCombined;
import com.example.drawling.domain.model.game.round.GameRound;
import org.springframework.stereotype.Service;

@Service
public class GameModeCombinedServiceImpl implements GameModeService<GameModeCombined> {
    public GameModeCombinedServiceImpl(GameRoundServiceFactory gameRoundServiceFactory) {
    }

    @Override
    public GameRound getActiveRound(GameModeCombined gameMode) {
        return null;
    }
}
