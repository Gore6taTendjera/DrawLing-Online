package com.example.drawling.business.game.round;

import com.example.drawling.business.implementation.game.round.GameRoundNormalServiceImpl;
import com.example.drawling.domain.model.game.round.GameRound;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GameRoundNormalServiceImplTest {

    @InjectMocks
    private GameRoundNormalServiceImpl gameRoundNormalService;

    @Mock
    private GameRound gameRound;

    @Test
    void testStartRound() {
        when(gameRound.getDuration()).thenReturn(30);
        gameRoundNormalService.startRound(gameRound);
        verify(gameRound).setActive(true);
        verify(gameRound).getDuration();
    }

    @Test
    void testEndRound() {
        gameRoundNormalService.endRound(gameRound);
        verify(gameRound).setActive(false);
    }

    @Test
    void testIsRoundActive() {
        when(gameRound.isActive()).thenReturn(true);
        boolean isActive = gameRoundNormalService.isRoundActive(gameRound);
        verify(gameRound).isActive();
        assertTrue(isActive);
    }
}