package com.example.drawling.integration.api;

import com.example.drawling.application.controller.game.WebSocketEventListener;
import com.example.drawling.application.controller.helper.GameNotificationsControllerHelper;
import com.example.drawling.application.controller.helper.PlayerControllerHelper;
import com.example.drawling.business.interfaces.service.game.GameSessionService;
import com.example.drawling.domain.model.Player;
import com.example.drawling.domain.model.game.GameSession;
import com.example.drawling.domain.model.game.PlayerSession;
import com.example.drawling.statics.GameStaticSessions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WebSocketEventListenerTest {
    @Mock
    private GameSessionService gameSessionService;

    @Mock
    private PlayerControllerHelper playerControllerHelper;

    @Mock
    private GameNotificationsControllerHelper gameNotificationsControllerHelper;

    @Mock
    private GameSession gameSession;

    @Mock
    private Player player;

    private WebSocketEventListener webSocketEventListener;

    @BeforeEach
    void setUp() {
        webSocketEventListener = new WebSocketEventListener(gameSessionService, playerControllerHelper, gameNotificationsControllerHelper);
    }

    @Test
    void testHandleWebSocketConnectListener() throws NoSuchFieldException, IllegalAccessException {
        // Arrange
        String sessionId = "sessionId123";

        SessionConnectEvent connectEvent = mock(SessionConnectEvent.class);

        Message<byte[]> message = mock(Message.class);

        MessageHeaders messageHeaders = mock(MessageHeaders.class);

        when(message.getHeaders()).thenReturn(messageHeaders);
        when(messageHeaders.get("simpSessionId")).thenReturn(sessionId);

        when(connectEvent.getMessage()).thenReturn(message);

        // Act
        webSocketEventListener.handleWebSocketConnectListener(connectEvent);

        Field sessionRegistryField = WebSocketEventListener.class.getDeclaredField("sessionRegistry");
        sessionRegistryField.setAccessible(true);

        @SuppressWarnings("unchecked")
        List<String> sessionRegistry = (List<String>) sessionRegistryField.get(null);

        // Assert
        assertTrue(sessionRegistry.contains(sessionId));
    }

    @Test
    void testHandleWebSocketDisconnectListener() {
        // Arrange
        String sessionId = "sessionId123";
        String playerDisplayName = "Player1";
        SessionDisconnectEvent disconnectEvent = mock(SessionDisconnectEvent.class);
        when(disconnectEvent.getSessionId()).thenReturn(sessionId);

        GameSession gs = mock(GameSession.class);

        Player mockPlayer = mock(Player.class);
        when(mockPlayer.getDisplayName()).thenReturn(playerDisplayName);

        PlayerSession mockPlayerSession = new PlayerSession(sessionId, mockPlayer);

        Map<String, PlayerSession> players = new HashMap<>();
        players.put(sessionId, mockPlayerSession);

        when(gs.getPlayers()).thenReturn(players);

        when(gameSessionService.getGameSessionByPlayerSessionId(sessionId)).thenReturn(gs);

        Map<String, GameSession> gameSessionMap = new HashMap<>();
        gameSessionMap.put("gameSessionId", gs);

        try (MockedStatic<GameStaticSessions> mockedStatic = mockStatic(GameStaticSessions.class)) {
            mockedStatic.when(GameStaticSessions::getReadOnlySessions).thenReturn(gameSessionMap);

            when(gameSessionService.kickPlayer(gs, sessionId)).thenReturn(true);

            // Act
            webSocketEventListener.handleWebSocketDisconnectListener(disconnectEvent);

            // Assert
            verify(gameNotificationsControllerHelper).sendPlayerLeftNotification((gs.getSessionId()), (playerDisplayName));
            verify(playerControllerHelper).sendActivePlayers(("gameSessionId"));
            verify(playerControllerHelper).sendPlayerRoles((gs));
        }
    }


    @Test
    void testHandleWebSocketDisconnectListener_PlayerNotFound() {
        // Arrange
        String sessionId = "sessionId123";
        SessionDisconnectEvent disconnectEvent = mock(SessionDisconnectEvent.class);
        when(disconnectEvent.getSessionId()).thenReturn(sessionId);

        when(gameSessionService.getGameSessionByPlayerSessionId(sessionId)).thenReturn(null);

        // Act
        webSocketEventListener.handleWebSocketDisconnectListener(disconnectEvent);

        // Assert
        verify(gameNotificationsControllerHelper, never()).sendPlayerLeftNotification(anyString(), anyString());
    }


    @Test
    void testHandleWebSocketDisconnectListener_FailToKickPlayer() {
        // Arrange
        String sessionId = "sessionId123";
        String playerDisplayName = "Player1";
        SessionDisconnectEvent disconnectEvent = mock(SessionDisconnectEvent.class);
        when(disconnectEvent.getSessionId()).thenReturn(sessionId);

        GameSession gs = mock(GameSession.class);

        Map<String, PlayerSession> players = new HashMap<>();
        Player mockPlayer = mock(Player.class);
        PlayerSession mockPlayerSession = new PlayerSession(sessionId, mockPlayer);
        when(mockPlayer.getDisplayName()).thenReturn(playerDisplayName);
        players.put(sessionId, mockPlayerSession);

        when(gs.getPlayers()).thenReturn(players);
        when(gameSessionService.getGameSessionByPlayerSessionId(sessionId)).thenReturn(gs);

        Map<String, GameSession> gameSessionMap = new HashMap<>();
        gameSessionMap.put("gameSessionId", gs);

        try (MockedStatic<GameStaticSessions> mockedStatic = mockStatic(GameStaticSessions.class)) {
            mockedStatic.when(GameStaticSessions::getReadOnlySessions).thenReturn(gameSessionMap);

            when(gameSessionService.kickPlayer(gs, sessionId)).thenReturn(false);

            // Act
            webSocketEventListener.handleWebSocketDisconnectListener(disconnectEvent);

            // Assert
            verify(gameNotificationsControllerHelper).sendPlayerLeftNotification((gs.getSessionId()), (playerDisplayName));
            verify(playerControllerHelper, never()).sendActivePlayers(anyString());
            verify(playerControllerHelper, never()).sendPlayerRoles(any(GameSession.class));
        }
    }

}