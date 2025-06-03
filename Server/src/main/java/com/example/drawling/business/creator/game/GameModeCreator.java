package com.example.drawling.business.creator.game;

import com.example.drawling.domain.dto.game.GameRoundRequestDTO;
import com.example.drawling.domain.enums.game.GameModeEnum;
import com.example.drawling.domain.model.game.mode.*;
import com.example.drawling.domain.model.game.round.GameRound;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GameModeCreator {
    private final GameRoundCreator gameRoundCreator;

    public GameModeCreator(GameRoundCreator gameRoundCreator) {
        this.gameRoundCreator = gameRoundCreator;
    }

    public GameMode createGameMode(int maxPlayers, List<GameRoundRequestDTO> gameRoundsRequest, GameModeEnum gameModeName) {
        if (maxPlayers <= 0) {
            throw new IllegalArgumentException("Max players must be greater than zero.");
        }
        if (gameRoundsRequest == null || gameRoundsRequest.isEmpty()) {
            throw new IllegalArgumentException("Game rounds request cannot be null or empty.");
        }
        if (gameModeName == null) {
            throw new IllegalArgumentException("Game mode name cannot be null.");
        }

        List<GameRound> gameRounds = gameRoundCreator.createGameRounds(gameRoundsRequest);

        return switch (gameModeName) {
            case NORMAL -> new GameModeNormal(maxPlayers, gameRounds);
            case DUO -> new GameModeDuo(maxPlayers, gameRounds);
            case TRIO -> new GameModeTrio(maxPlayers, gameRounds);
            case COMBINED -> new GameModeCombined(maxPlayers, gameRounds);
            default -> throw new IllegalArgumentException("Invalid game mode: " + gameModeName);
        };
    }


}
