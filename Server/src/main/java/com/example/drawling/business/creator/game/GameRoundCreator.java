package com.example.drawling.business.creator.game;

import com.example.drawling.business.interfaces.service.game.GameWordGeneratorService;
import com.example.drawling.domain.dto.game.GameRoundRequestDTO;
import com.example.drawling.domain.enums.game.GameRoundEnum;
import com.example.drawling.domain.enums.game.GameWordCategoryEnum;
import com.example.drawling.domain.model.game.round.GameRound;
import com.example.drawling.domain.model.game.round.GameRoundFast;
import com.example.drawling.domain.model.game.round.GameRoundNormal;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GameRoundCreator {

    private final GameWordGeneratorService gameWordGeneratorService;

    public GameRoundCreator(GameWordGeneratorService gameWordGeneratorService) {
        this.gameWordGeneratorService = gameWordGeneratorService;
    }

    public List<GameRound> createGameRounds(List<GameRoundRequestDTO> gameRoundsRequest) {
        if (gameRoundsRequest == null || gameRoundsRequest.isEmpty()) {
            throw new IllegalArgumentException("Game rounds request list cannot be null or empty.");
        }

        List<GameRound> gameRounds = new ArrayList<>();

        for (GameRoundRequestDTO roundRequest : gameRoundsRequest) {
            if (roundRequest == null) {
                throw new IllegalArgumentException("Game round request cannot be null.");
            }

            int duration = roundRequest.getDuration();
            if (duration <= 0) {
                throw new IllegalArgumentException("Duration must be greater than zero.");
            }

            GameRoundEnum roundEnum = roundRequest.getGameRoundEnum();
            if (roundEnum == null) {
                throw new IllegalArgumentException("Game round enum cannot be null.");
            }

            GameWordCategoryEnum wordCategory = roundRequest.getWordCategory();
            String word;
            if (wordCategory == null) {
                word = gameWordGeneratorService.generateRandomWordFromRandomCategory();
            } else {
                word = gameWordGeneratorService.generateRandomWordFromCategory(wordCategory);
            }

            GameRound gameRound = switch (roundEnum) {
                case NORMAL -> new GameRoundNormal(duration, word);
                case FAST -> new GameRoundFast(duration, word);
                default -> throw new IllegalArgumentException("Unsupported game round type: " + roundEnum);
            };
            gameRounds.add(gameRound);
        }

        return gameRounds;
    }
}