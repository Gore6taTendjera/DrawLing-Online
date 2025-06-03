package com.example.drawling.integration.api.game;

import com.example.drawling.application.controller.game.ChatController;
import com.example.drawling.business.interfaces.service.game.GameSessionService;
import com.example.drawling.domain.dto.game.ChatMessageDTO;
import com.example.drawling.domain.enums.game.GamePlayerRole;
import com.example.drawling.domain.model.Player;
import com.example.drawling.domain.model.game.GameSession;
import com.example.drawling.domain.model.game.PlayerSession;
import com.example.drawling.domain.model.game.round.GameRound;
import com.example.drawling.statics.GameStaticSessions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ChatControllerTest {

    @InjectMocks
    private ChatController chatController;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @Mock
    private GameSessionService gameSessionService;

    @Mock
    private GameSession gameSession;

    @Mock
    private PlayerSession playerSession;

    @Mock
    private GameRound activeGameRound;

    @Mock
    private SimpMessageHeaderAccessor headerAccessor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    void chatMessage_PlayerGuessIncorrectly_SendsMessage() {
        String roomId = "room1";
        String incorrectGuess = "wrongWord";
        String sessionId = "sessionId";
        String playerName = "Player1";

        Player playerMock = Mockito.mock(Player.class);

        when(headerAccessor.getSessionId()).thenReturn(sessionId);

        try (MockedStatic<GameStaticSessions> mockedStatic = Mockito.mockStatic(GameStaticSessions.class)) {
            mockedStatic.when(() -> GameStaticSessions.getByLink(roomId)).thenReturn(gameSession);

            when(gameSessionService.getPlayerBySessionAndPlayerSessionId(gameSession, sessionId)).thenReturn(playerSession);
            when(playerSession.getRole()).thenReturn(GamePlayerRole.GUESSING);

            when(playerSession.getPlayer()).thenReturn(playerMock);
            when(playerMock.getDisplayName()).thenReturn(playerName);

            when(gameSessionService.getActiveGameRound(gameSession)).thenReturn(activeGameRound);
            when(activeGameRound.getWord()).thenReturn("word");

            chatController.chatMessage(roomId, incorrectGuess, headerAccessor);

            ArgumentCaptor<ChatMessageDTO> messageCaptor = ArgumentCaptor.forClass(ChatMessageDTO.class);
            verify(messagingTemplate).convertAndSend(eq("/topic/chat/receive/room/" + roomId), messageCaptor.capture());
            assertEquals(playerName, messageCaptor.getValue().getPlayerName());
            assertEquals(incorrectGuess, messageCaptor.getValue().getText());
        }
    }



    @Test
    void chatMessage_PlayerRoleNotGuessing_DoesNotSendMessage() {
        String roomId = "room1";
        String sessionId = "sessionId";

        when(headerAccessor.getSessionId()).thenReturn(sessionId);

        try (MockedStatic<GameStaticSessions> mockedStatic = Mockito.mockStatic(GameStaticSessions.class)) {
            mockedStatic.when(() -> GameStaticSessions.getByLink(roomId)).thenReturn(gameSession);

            when(gameSessionService.getPlayerBySessionAndPlayerSessionId(gameSession, sessionId)).thenReturn(playerSession);
            when(playerSession.getRole()).thenReturn(GamePlayerRole.DRAWING);

            chatController.chatMessage(roomId, "some message", headerAccessor);

            verify(messagingTemplate, never()).convertAndSend(anyString(), any(ChatMessageDTO.class));
        }
    }

}