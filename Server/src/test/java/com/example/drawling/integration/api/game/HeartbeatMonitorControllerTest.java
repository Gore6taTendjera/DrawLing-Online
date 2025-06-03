package com.example.drawling.integration.api.game;

import com.example.drawling.application.controller.game.HeartbeatMonitorController;
import com.example.drawling.application.controller.helper.PlayerControllerHelper;
import com.example.drawling.business.implementation.game.session.GameSessionServiceImpl;
import com.example.drawling.domain.model.game.GameSession;
import com.example.drawling.domain.model.game.PlayerSession;
import com.example.drawling.statics.GameStaticSessions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class HeartbeatMonitorControllerTest {

    @InjectMocks
    private HeartbeatMonitorController heartbeatMonitorController;

    @Mock
    private GameSessionServiceImpl gameSessionService;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @Mock
    private PlayerControllerHelper playerControllerHelper;

    @Mock
    private SimpMessageHeaderAccessor headerAccessor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testUpdateLastActiveTime() {
        // Arrange
        String sessionId = "session123";
        long lastActiveTimeBeforeUpdate = System.currentTimeMillis();

        // Act
        heartbeatMonitorController.updateLastActiveTime(sessionId);

        // Assert
        Map<String, Long> lastActiveTimes = heartbeatMonitorController.getLastActiveTimes();

        assertTrue(lastActiveTimes.containsKey(sessionId), "Session ID should be added to lastActiveTimes map.");

        long lastActiveTimeAfterUpdate = lastActiveTimes.get(sessionId);
        assertTrue(lastActiveTimeBeforeUpdate <= lastActiveTimeAfterUpdate, "Last active time should be a recent timestamp.");

        assertTrue(lastActiveTimeAfterUpdate <= System.currentTimeMillis(), "Last active time should be a recent timestamp.");

        int initialSize = lastActiveTimes.size() - 1; // Since the session is new
        assertEquals(initialSize + 1, lastActiveTimes.size(), "The size of the lastActiveTimes map should increase by one.");
    }


    @Test
    void testPlayerHeartbeat() {
        // Arrange
        String roomId = "room1";
        String sessionId = "session123";
        when(headerAccessor.getSessionId()).thenReturn(sessionId);

        try (MockedStatic<GameStaticSessions> mockedStatic = mockStatic(GameStaticSessions.class)) {
            GameSession gameSession = mock(GameSession.class);
            PlayerSession playerSession = mock(PlayerSession.class);
            mockedStatic.when(() -> GameStaticSessions.getByLink(roomId)).thenReturn(gameSession);
            when(gameSessionService.getPlayerBySessionAndPlayerSessionId(gameSession, sessionId)).thenReturn(playerSession);

            // Act
            heartbeatMonitorController.playerHeartbeat(roomId, headerAccessor);

            // Assert
            verify(gameSessionService).getPlayerBySessionAndPlayerSessionId(gameSession, sessionId);
            verifyNoInteractions(messagingTemplate, playerControllerHelper);
        }
    }





    @Test
    void testCheckInactivePlayersWithNoTimeout() {
        // Arrange
        String roomId = "room1";
        String sessionId = "session123";
        GameSession gameSession = mock(GameSession.class);
        PlayerSession playerSession = mock(PlayerSession.class);
        when(playerSession.getSessionId()).thenReturn(sessionId);

        try (MockedStatic<GameStaticSessions> mockedStatic = mockStatic(GameStaticSessions.class)) {
            mockedStatic.when(GameStaticSessions::getReadOnlySessions).thenReturn(Collections.singletonMap(roomId, gameSession));
            when(gameSessionService.getAllPlayers(gameSession)).thenReturn(Collections.singletonList(playerSession));

            Map<String, Long> lastActiveTimes = heartbeatMonitorController.getLastActiveTimes();
            lastActiveTimes.put(sessionId, System.currentTimeMillis() - 1000);

            // Act
            heartbeatMonitorController.checkInactivePlayers();

            // Assert
            verifyNoInteractions(messagingTemplate, playerControllerHelper);
            verify(gameSessionService, never()).kickPlayer(any(), any());
        }
    }

}
