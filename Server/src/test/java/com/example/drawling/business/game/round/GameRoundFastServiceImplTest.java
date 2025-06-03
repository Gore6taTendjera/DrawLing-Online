package com.example.drawling.business.game.round;

import com.example.drawling.business.implementation.game.round.GameRoundFastServiceImpl;
import com.example.drawling.domain.model.game.round.GameRound;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GameRoundFastServiceImplTest {

    @InjectMocks
    private GameRoundFastServiceImpl gameRoundFastService;

    @Mock
    private GameRound gameRound;

    @Test
    void testStartRound() {
        gameRoundFastService.startRound(gameRound);
        verify(gameRound).setActive(true);
    }

    @Test
    void testEndRound() {
        gameRoundFastService.endRound(gameRound);
        verify(gameRound).setActive(false);
    }

    @Test
    void testIsRoundActive() {
        when(gameRound.isActive()).thenReturn(false);
        boolean isActive = gameRoundFastService.isRoundActive(gameRound);
        verify(gameRound).isActive();
        assertFalse(isActive);
    }
}