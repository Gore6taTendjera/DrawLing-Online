package com.example.drawling.integration.api.game;

import com.example.drawling.application.controller.game.GameWordController;
import com.example.drawling.application.controller.helper.GameNotificationsControllerHelper;
import com.example.drawling.business.interfaces.service.game.GameSessionService;
import com.example.drawling.domain.enums.game.GamePlayerRole;
import com.example.drawling.domain.model.Player;
import com.example.drawling.domain.model.game.GameSession;
import com.example.drawling.domain.model.game.PlayerSession;
import com.example.drawling.domain.model.game.round.GameRound;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GameWordControllerTest {

    @Mock
    private GameSessionService gameSessionService;

    @Mock
    private GameNotificationsControllerHelper gameNotificationsControllerHelper;

    @Mock
    private SimpMessageHeaderAccessor headerAccessor;

    @Mock
    private PlayerSession playerSession;

    @Mock
    private GameSession gameSession;

    @Mock
    private GameRound activeGameRound;

    private GameWordController gameWordController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        gameWordController = new GameWordController(gameSessionService, gameNotificationsControllerHelper);
    }

    @Test
    void sendWord_PlayerIsDrawing_ReturnsWord() {
        String playerId = "sessionId";
        String expectedWord = "example";

        when(headerAccessor.getSessionId()).thenReturn(playerId);
        when(gameSessionService.getPlayerBySessionId(playerId)).thenReturn(playerSession);
        when(playerSession.getRole()).thenReturn(GamePlayerRole.DRAWING);
        when(gameSessionService.getGameSessionByPlayerSessionId(playerId)).thenReturn(gameSession);
        when(gameSessionService.getActiveGameRound(gameSession)).thenReturn(activeGameRound);
        when(activeGameRound.getWord()).thenReturn(expectedWord);

        String result = gameWordController.sendWord(headerAccessor);

        assertEquals(expectedWord, result);
    }

    @Test
    void sendWord_PlayerIsGuessing_ReturnsUnderscores() {
        String playerId = "sessionId";
        String expectedWord = "example";
        String expectedUnderscores = "_ _ _ _ _ _ _";

        when(headerAccessor.getSessionId()).thenReturn(playerId);
        when(gameSessionService.getPlayerBySessionId(playerId)).thenReturn(playerSession);
        when(playerSession.getRole()).thenReturn(GamePlayerRole.GUESSING);
        when(gameSessionService.getGameSessionByPlayerSessionId(playerId)).thenReturn(gameSession);
        when(gameSessionService.getActiveGameRound(gameSession)).thenReturn(activeGameRound);
        when(activeGameRound.getWord()).thenReturn(expectedWord);

        String result = gameWordController.sendWord(headerAccessor);

        assertEquals(expectedUnderscores, result);
    }


    @Test
    void wordGuessing_CorrectGuess_SendsNotification() {
        String playerId = "sessionId";
        String guessedWord = "example";
        String sessionId = "gameSessionId";

        Player playerMock = mock(Player.class);
        when(playerMock.getDisplayName()).thenReturn("Player1");

        when(headerAccessor.getSessionId()).thenReturn(playerId);
        when(gameSessionService.getPlayerBySessionId(playerId)).thenReturn(playerSession);
        when(playerSession.getRole()).thenReturn(GamePlayerRole.GUESSING);
        when(playerSession.getPlayer()).thenReturn(playerMock);
        when(gameSessionService.getGameSessionByPlayerSessionId(playerId)).thenReturn(gameSession);
        when(gameSession.getSessionId()).thenReturn(sessionId);
        when(gameSessionService.getActiveGameRound(gameSession)).thenReturn(activeGameRound);
        when(activeGameRound.getWord()).thenReturn(guessedWord);

        boolean result = gameWordController.wordGuessing(headerAccessor, guessedWord);

        assertTrue(result);
        verify(gameNotificationsControllerHelper).sendPlayerGuessedNotification(sessionId, "Player1");

    }


    @Test
    void wordGuessing_PlayerIsNotGuessing_ReturnsFalse() {
        String playerId = "sessionId";
        String guessedWord = "example";

        when(headerAccessor.getSessionId()).thenReturn(playerId);
        when(gameSessionService.getPlayerBySessionId(playerId)).thenReturn(playerSession);
        when(playerSession.getRole()).thenReturn(GamePlayerRole.DRAWING);
        when(gameSessionService.getGameSessionByPlayerSessionId(playerId)).thenReturn(gameSession);
        when(gameSessionService.getActiveGameRound(gameSession)).thenReturn(activeGameRound);
        when(activeGameRound.getWord()).thenReturn("example");

        boolean result = gameWordController.wordGuessing(headerAccessor, guessedWord);

        assertFalse(result);
        verify(gameNotificationsControllerHelper, never()).sendPlayerGuessedNotification(anyString(), anyString());
    }
}
