package com.example.drawling.business.game.mode;

import com.example.drawling.business.implementation.game.mode.GameModeDuoServiceImpl;
import com.example.drawling.domain.model.game.mode.GameModeDuo;
import com.example.drawling.domain.model.game.round.GameRound;
import com.example.drawling.domain.model.game.round.GameRoundFast;
import com.example.drawling.domain.model.game.round.GameRoundNormal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
class GameModeDuoServiceImplTest {

    @InjectMocks
    private GameModeDuoServiceImpl gameModeDuoService;

    @Test
    void testGetActiveRound_ReturnsNull_WhenNoActiveRound() {
        // Arrange
        List<GameRound> gameRounds = Arrays.asList(
                new GameRoundNormal(60),
                new GameRoundFast(30)
        );
        GameModeDuo gameMode = new GameModeDuo(2, gameRounds);

        // Act
        GameRound result = gameModeDuoService.getActiveRound(gameMode);

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
        GameModeDuo gameMode = new GameModeDuo(2, gameRounds);

        // Act
        GameRound result = gameModeDuoService.getActiveRound(gameMode);

        // Assert
        assertNull(result);
    }
}