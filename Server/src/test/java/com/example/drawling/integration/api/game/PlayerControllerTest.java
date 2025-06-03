package com.example.drawling.integration.api.game;

import com.example.drawling.application.controller.game.PlayerController;
import com.example.drawling.application.controller.helper.GameNotificationsControllerHelper;
import com.example.drawling.application.controller.helper.PlayerControllerHelper;
import com.example.drawling.application.handler.PlayerHandler;
import com.example.drawling.business.interfaces.service.game.GameSessionService;
import com.example.drawling.business.manager.GameSessionManager;
import com.example.drawling.domain.dto.game.ActivePlayerDTO;
import com.example.drawling.domain.dto.game.PlayerJoinRequestDTO;
import com.example.drawling.domain.enums.game.GamePlayerRole;
import com.example.drawling.domain.model.Player;
import com.example.drawling.domain.model.game.GameSession;
import com.example.drawling.domain.model.game.PlayerSession;
import com.example.drawling.statics.GameStaticSessions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlayerControllerTest {

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @Mock
    private GameSessionManager gameSessionManager;

    @Mock
    private GameSessionService gameSessionService;

    @Mock
    private GameNotificationsControllerHelper gameNotificationsControllerHelper;

    @Mock
    private PlayerControllerHelper playerControllerHelper;

    @Mock
    private PlayerHandler playerHandler;

    @InjectMocks
    private PlayerController playerController;

    @Test
    void testIsRoomFull_RoomExistsAndFull() {
        String roomId = "testRoom";
        GameSession gameSession = mock(GameSession.class);

        try (MockedStatic<GameStaticSessions> mockedStatic = mockStatic(GameStaticSessions.class)) {
            mockedStatic.when(() -> GameStaticSessions.getByLink(roomId)).thenReturn(gameSession);
            when(gameSessionService.isMaxPlayersReached(gameSession)).thenReturn(true);

            ResponseEntity<Void> response = playerController.isRoomFull(roomId);

            assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        }
    }

    @Test
    void testIsRoomFull_RoomExistsAndNotFull() {
        String roomId = "testRoom";
        GameSession gameSession = mock(GameSession.class);

        try (MockedStatic<GameStaticSessions> mockedStatic = mockStatic(GameStaticSessions.class)) {
            mockedStatic.when(() -> GameStaticSessions.getByLink(roomId)).thenReturn(gameSession);
            when(gameSessionService.isMaxPlayersReached(gameSession)).thenReturn(false);

            ResponseEntity<Void> response = playerController.isRoomFull(roomId);

            assertEquals(HttpStatus.OK, response.getStatusCode());
        }
    }

    @Test
    void testIsRoomFull_RoomDoesNotExist() {
        String roomId = "nonExistentRoom";

        try (MockedStatic<GameStaticSessions> mockedStatic = mockStatic(GameStaticSessions.class)) {
            mockedStatic.when(() -> GameStaticSessions.getByLink(roomId)).thenReturn(null);

            ResponseEntity<Void> response = playerController.isRoomFull(roomId);

            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        }
    }

    @Test
    void testGetActivePlayersInRoom_RoomExists() {
        String roomId = "testRoom";
        GameSession gameSession = mock(GameSession.class);

        List<PlayerSession> playerSessions = List.of(
                new PlayerSession("session1", new Player("Player1")),
                new PlayerSession("session2", new Player("Player2"))
        );
        playerSessions.get(0).setRole(GamePlayerRole.DRAWING);
        playerSessions.get(1).setRole(GamePlayerRole.GUESSING);

        try (MockedStatic<GameStaticSessions> mockedStatic = mockStatic(GameStaticSessions.class)) {
            mockedStatic.when(() -> GameStaticSessions.getByLink(roomId)).thenReturn(gameSession);
            when(gameSessionService.getAllPlayers(gameSession)).thenReturn(playerSessions);

            ResponseEntity<List<ActivePlayerDTO>> response = playerController.getActivePlayersInRoom(roomId);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(2, response.getBody().size());
        }
    }

    @Test
    void testGetActivePlayersInRoom_RoomDoesNotExist() {
        String roomId = "nonExistentRoom";

        try (MockedStatic<GameStaticSessions> mockedStatic = mockStatic(GameStaticSessions.class)) {
            mockedStatic.when(() -> GameStaticSessions.getByLink(roomId)).thenReturn(null);

            ResponseEntity<List<ActivePlayerDTO>> response = playerController.getActivePlayersInRoom(roomId);

            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        }
    }

    @Test
    void testPlayerJoin_Success() {
        String roomId = "testRoom";
        String playerName = "Player1";
        PlayerJoinRequestDTO requestDTO = new PlayerJoinRequestDTO(playerName, null);
        SimpMessageHeaderAccessor headerAccessor = mock(SimpMessageHeaderAccessor.class);

        GameSession gameSession = mock(GameSession.class);
        when(headerAccessor.getSessionId()).thenReturn("sessionId");
        when(gameSessionService.isMaxPlayersReached(gameSession)).thenReturn(false);

        try (MockedStatic<GameStaticSessions> mockedStatic = mockStatic(GameStaticSessions.class)) {
            mockedStatic.when(() -> GameStaticSessions.getByLink(roomId)).thenReturn(gameSession);

            playerController.playerJoin(roomId, requestDTO, headerAccessor);

            verify(gameSessionService).addPlayer(eq(gameSession), any(PlayerSession.class));
            verify(gameNotificationsControllerHelper).sendPlayerJoinNotification((roomId), (playerName));
        }
    }

    @Test
    void testPlayerJoin_RoomFull() {
        String roomId = "testRoom";
        String playerName = "Player1";
        PlayerJoinRequestDTO requestDTO = new PlayerJoinRequestDTO(playerName, null);
        SimpMessageHeaderAccessor headerAccessor = mock(SimpMessageHeaderAccessor.class);

        GameSession gameSession = mock(GameSession.class);

        try (MockedStatic<GameStaticSessions> mockedStatic = mockStatic(GameStaticSessions.class)) {
            mockedStatic.when(() -> GameStaticSessions.getByLink(roomId)).thenReturn(gameSession);
            when(gameSessionService.isMaxPlayersReached(gameSession)).thenReturn(true); // Keep this if it's used

            playerController.playerJoin(roomId, requestDTO, headerAccessor);

            verify(messagingTemplate).convertAndSendToUser((playerName), ("/topic/sendSession"), ("FULL"));
            verify(gameSessionService, never()).addPlayer(any(), any());
        }
    }



    @Test
    void testPlayerJoin_GameSessionIsNull() {
        String roomId = "nonExistentRoom";
        PlayerJoinRequestDTO requestDTO = new PlayerJoinRequestDTO("Player1", null);
        SimpMessageHeaderAccessor headerAccessor = mock(SimpMessageHeaderAccessor.class);

        try (MockedStatic<GameStaticSessions> mockedStatic = mockStatic(GameStaticSessions.class)) {
            mockedStatic.when(() -> GameStaticSessions.getByLink(roomId)).thenReturn(null);

            playerController.playerJoin(roomId, requestDTO, headerAccessor);

            verifyNoInteractions(gameSessionService, messagingTemplate, gameNotificationsControllerHelper, gameSessionManager, playerHandler);
        }
    }

    @Test
    void testPlayerJoin_UserIdIsNull() {
        String roomId = "testRoom";
        String playerName = "Player1";
        PlayerJoinRequestDTO requestDTO = new PlayerJoinRequestDTO(playerName, null);
        SimpMessageHeaderAccessor headerAccessor = mock(SimpMessageHeaderAccessor.class);

        GameSession gameSession = mock(GameSession.class);
        when(headerAccessor.getSessionId()).thenReturn("sessionId");
        when(gameSessionService.isMaxPlayersReached(gameSession)).thenReturn(false);

        try (MockedStatic<GameStaticSessions> mockedStatic = mockStatic(GameStaticSessions.class)) {
            mockedStatic.when(() -> GameStaticSessions.getByLink(roomId)).thenReturn(gameSession);

            playerController.playerJoin(roomId, requestDTO, headerAccessor);

            ArgumentCaptor<PlayerSession> captor = ArgumentCaptor.forClass(PlayerSession.class);
            verify(gameSessionService).addPlayer(eq(gameSession), captor.capture());
            PlayerSession capturedSession = captor.getValue();

            assertEquals(playerName, capturedSession.getPlayer().getDisplayName());
            assertEquals(0, capturedSession.getPlayer().getId());
        }
    }

    @Test
    void testPlayerJoin_UserIdIsNotNull() {
        String roomId = "testRoom";
        String playerName = "Player1";
        Integer userId = 123;
        PlayerJoinRequestDTO requestDTO = new PlayerJoinRequestDTO(playerName, userId);
        SimpMessageHeaderAccessor headerAccessor = mock(SimpMessageHeaderAccessor.class);

        GameSession gameSession = mock(GameSession.class);
        when(headerAccessor.getSessionId()).thenReturn("sessionId");
        when(gameSessionService.isMaxPlayersReached(gameSession)).thenReturn(false);

        try (MockedStatic<GameStaticSessions> mockedStatic = mockStatic(GameStaticSessions.class)) {
            mockedStatic.when(() -> GameStaticSessions.getByLink(roomId)).thenReturn(gameSession);

            playerController.playerJoin(roomId, requestDTO, headerAccessor);

            ArgumentCaptor<PlayerSession> captor = ArgumentCaptor.forClass(PlayerSession.class);
            verify(gameSessionService).addPlayer(eq(gameSession), captor.capture());
            PlayerSession capturedSession = captor.getValue();

            assertEquals(playerName, capturedSession.getPlayer().getDisplayName());
            assertEquals(userId, capturedSession.getPlayer().getId()); // Player ID matches userId
        }
    }


    @Test
    void testGetPlayers() {
        String roomId = "testRoom";

        playerController.getPlayers(roomId);

        verify(playerControllerHelper).sendActivePlayers((roomId));
    }

    @Test
    void testGetPlayerRole() {
        String roomId = "testRoom";
        String sessionId = "sessionId";
        SimpMessageHeaderAccessor headerAccessor = mock(SimpMessageHeaderAccessor.class);

        GameSession gameSession = mock(GameSession.class);
        PlayerSession playerSession = new PlayerSession(sessionId, new Player("Player1"));
        playerSession.setRole(GamePlayerRole.DRAWING);

        when(headerAccessor.getSessionId()).thenReturn(sessionId);
        when(gameSessionService.getPlayerBySessionAndPlayerSessionId((gameSession), (sessionId))).thenReturn(playerSession);

        try (MockedStatic<GameStaticSessions> mockedStatic = mockStatic(GameStaticSessions.class)) {
            mockedStatic.when(() -> GameStaticSessions.getByLink(roomId)).thenReturn(gameSession);

            GamePlayerRole role = playerController.getPlayerRole(roomId, headerAccessor);

            assertEquals(GamePlayerRole.DRAWING, role);
        }
    }
}
