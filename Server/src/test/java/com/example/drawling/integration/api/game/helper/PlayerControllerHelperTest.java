package com.example.drawling.integration.api.game.helper;

import com.example.drawling.application.controller.helper.PlayerControllerHelper;
import com.example.drawling.business.interfaces.service.game.GameSessionService;
import com.example.drawling.domain.dto.game.ActivePlayerDTO;
import com.example.drawling.domain.enums.game.GamePlayerRole;
import com.example.drawling.domain.model.Player;
import com.example.drawling.domain.model.game.GameSession;
import com.example.drawling.domain.model.game.PlayerSession;
import com.example.drawling.statics.GameStaticSessions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlayerControllerHelperTest {
    @Mock
    private GameSessionService gameSessionService;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private PlayerControllerHelper playerControllerHelper;

    @Captor
    private ArgumentCaptor<String> destinationCaptor;

    @Captor
    private ArgumentCaptor<Object> payloadCaptor;

    private static final String ROOM_ID = "room1";

    @BeforeEach
    void setUp() {
        // mocks
    }

    @Test
    void testSendActivePlayers() {
        // Arrange
        PlayerSession playerSession1 = new PlayerSession("session1", new Player("Player1", 100.0, "url1"));
        PlayerSession playerSession2 = new PlayerSession("session2", new Player("Player2", 200.0, "url2"));
        List<PlayerSession> playerSessions = List.of(playerSession1, playerSession2);

        GameSession mockedGameSession = new GameSession();

        try (MockedStatic<GameStaticSessions> gameStaticSessionsMock = mockStatic(GameStaticSessions.class)) {
            gameStaticSessionsMock.when(() -> GameStaticSessions.getByLink(ROOM_ID)).thenReturn(mockedGameSession);

            when(gameSessionService.getAllPlayers(mockedGameSession)).thenReturn(playerSessions);

            // Act
            playerControllerHelper.sendActivePlayers(ROOM_ID);

            // Assert
            verify(messagingTemplate).convertAndSend(destinationCaptor.capture(), payloadCaptor.capture());
            assertEquals("/topic/room/receive/activePlayers/" + ROOM_ID, destinationCaptor.getValue());

            @SuppressWarnings("unchecked")
            List<ActivePlayerDTO> activePlayers = (List<ActivePlayerDTO>) payloadCaptor.getValue();
            assertEquals(2, activePlayers.size());
            assertEquals("Player1", activePlayers.getFirst().getPlayerName());
            assertEquals(100.0, activePlayers.getFirst().getBalance());
            assertEquals("url1", activePlayers.getFirst().getProfilePicture());
        }
    }


    @Test
    void testSendPlayerRoles() {
        // Arrange
        PlayerSession playerSession1 = mock(PlayerSession.class);
        PlayerSession playerSession2 = mock(PlayerSession.class);

        GamePlayerRole role1 = GamePlayerRole.GUESSING;
        GamePlayerRole role2 = GamePlayerRole.DRAWING;

        when(playerSession1.getSessionId()).thenReturn("session1");
        when(playerSession2.getSessionId()).thenReturn("session2");

        when(playerSession1.getRole()).thenReturn(role1);
        when(playerSession2.getRole()).thenReturn(role2);

        List<PlayerSession> playerSessions = List.of(playerSession1, playerSession2);
        GameSession gameSession = mock(GameSession.class);

        when(gameSessionService.getAllPlayers(gameSession)).thenReturn(playerSessions);

        // Act
        playerControllerHelper.sendPlayerRoles(gameSession);

        // Assert
        verify(messagingTemplate).convertAndSendToUser("session1", "/topic/player/role", role1);
        verify(messagingTemplate).convertAndSendToUser("session2", "/topic/player/role", role2);
    }
}
