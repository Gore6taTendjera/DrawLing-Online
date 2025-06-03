package com.example.drawling.integration.api.game;

import com.example.drawling.application.controller.game.GameRoundController;
import com.example.drawling.application.controller.helper.GameRoundControllerHelper;
import com.example.drawling.business.interfaces.service.game.GameSessionService;
import com.example.drawling.domain.model.game.GameSession;
import com.example.drawling.domain.model.game.mode.GameMode;
import com.example.drawling.domain.model.game.round.GameRound;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GameRoundControllerTest {

    private GameRoundController gameRoundController;
    @Mock
    private GameRoundControllerHelper gameRoundControllerHelper;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @Mock
    private GameSessionService gameSessionService;

    @Mock
    private SimpMessageHeaderAccessor headerAccessor;

    @BeforeEach
    public void setUp() {
        gameRoundController = new GameRoundController(gameSessionService);
    }

    @Test
    void testGetRoundNumberForRoom() {
        // Arrange
        String playerId = "testPlayerId";
        GameSession gameSession = mock(GameSession.class);
        GameRound activeGameRound = mock(GameRound.class);
        GameRound anotherGameRound = mock(GameRound.class);
        GameMode gameMode = mock(GameMode.class);

        when(headerAccessor.getSessionId()).thenReturn(playerId);
        when(gameSessionService.getGameSessionByPlayerSessionId(playerId)).thenReturn(gameSession);
        when(gameSessionService.getActiveGameRound(gameSession)).thenReturn(activeGameRound);

        when(gameSession.getGameMode()).thenReturn(gameMode);

        when(gameMode.getGameRounds()).thenReturn(List.of(activeGameRound, anotherGameRound));

        // Act
        int roundNumber = gameRoundController.getRoundNumberForRoom(headerAccessor);

        // Assert
        assertEquals(1, roundNumber);
    }


}