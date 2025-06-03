package com.example.drawling.business.game.mode;

import com.example.drawling.business.implementation.game.mode.GameModeNormalServiceImpl;
import com.example.drawling.domain.model.game.mode.GameModeNormal;
import com.example.drawling.domain.model.game.round.GameRound;
import com.example.drawling.domain.model.game.round.GameRoundFast;
import com.example.drawling.domain.model.game.round.GameRoundNormal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class GameModeNormalServiceImplTest {

    @InjectMocks
    private GameModeNormalServiceImpl gameModeNormalService;


    @Test
    void testGetActiveRound_ReturnsActiveRound() {
        // Arrange
        GameRoundNormal activeRound = new GameRoundNormal(60);
        activeRound.setActive(true);
        List<GameRound> gameRounds = Arrays.asList(
                activeRound,
                new GameRoundFast(30)
        );
        GameModeNormal gameMode = new GameModeNormal(10, gameRounds);

        // Act
        GameRound result = gameModeNormalService.getActiveRound(gameMode);

        // Assert
        assertEquals(activeRound, result);
    }

    @Test
    void testGetActiveRound_ReturnsFirstActiveRound_WhenMultipleActiveRounds() {
        // Arrange
        GameRoundNormal firstActiveRound = new GameRoundNormal(60);
        firstActiveRound.setActive(true);
        GameRoundNormal secondActiveRound = new GameRoundNormal(30);
        secondActiveRound.setActive(true);
        List<GameRound> gameRounds = Arrays.asList(
                firstActiveRound,
                secondActiveRound
        );
        GameModeNormal gameMode = new GameModeNormal(10, gameRounds);

        // Act
        GameRound result = gameModeNormalService.getActiveRound(gameMode);

        // Assert
        assertEquals(firstActiveRound, result);
    }

}