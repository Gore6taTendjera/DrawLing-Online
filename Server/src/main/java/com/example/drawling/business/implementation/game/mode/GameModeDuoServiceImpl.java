
package com.example.drawling.business.implementation.game.mode;

import com.example.drawling.business.interfaces.factory.game.GameRoundServiceFactory;
import com.example.drawling.business.interfaces.service.game.GameModeService;
import com.example.drawling.domain.model.game.mode.GameModeDuo;
import com.example.drawling.domain.model.game.round.GameRound;

public class GameModeDuoServiceImpl implements GameModeService<GameModeDuo> {
    public GameModeDuoServiceImpl(GameRoundServiceFactory gameRoundServiceFactory) {
    }


    @Override
    public GameRound getActiveRound(GameModeDuo gameMode) {
        return null;
    }
}
