package com.example.drawling.business.game.mode;


import com.example.drawling.business.implementation.game.mode.GameModeTrioServiceImpl;
import com.example.drawling.domain.model.game.mode.GameModeTrio;
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
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
class GameModeTrioServiceImplTest {

    @InjectMocks
    private GameModeTrioServiceImpl gameModeTrioService;

    @Test
    void testGetActiveRound_ReturnsNull_WhenNoActiveRound() {
        // Arrange
        List<GameRound> gameRounds = Arrays.asList(
                new GameRoundNormal(60),
                new GameRoundFast(30)
        );
        GameModeTrio gameMode = new GameModeTrio(10, gameRounds);

        // Act
        GameRound result = gameModeTrioService.getActiveRound(gameMode);

        // Assert
        assertNull(result);
    }

    @Test
    void testGetActiveRound_ReturnsActiveRound() {
        // Arrange
        GameRoundNormal activeRound = new GameRoundNormal(60);
        activeRound.setActive(true);
        List<GameRound> gameRounds = Arrays.asList(
                activeRound,
                new GameRoundFast(30)
        );
        GameModeTrio gameMode = new GameModeTrio(10, gameRounds);

        // Act
        GameRound result = gameModeTrioService.getActiveRound(gameMode);

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
        GameModeTrio gameMode = new GameModeTrio(10, gameRounds);

        // Act
        GameRound result = gameModeTrioService.getActiveRound(gameMode);

        // Assert
        assertEquals(firstActiveRound, result);
    }
}
