package com.example.drawling.integration.api.game.helper;

import com.example.drawling.application.controller.helper.GameNotificationsControllerHelper;
import com.example.drawling.domain.enums.game.GameSessionState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class GameNotificationsControllerHelperTest {

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private GameNotificationsControllerHelper controllerHelper;

    private static final String ROOM_ID = "room1";
    private static final String PLAYER_NAME = "player1";
    private static final int ROUND_NUMBER = 1;
    private static final String WORD = "example";
    private static final GameSessionState GAME_STATE = GameSessionState.ACTIVE;

    @Captor
    private ArgumentCaptor<String> destinationCaptor;

    @Captor
    private ArgumentCaptor<Object> payloadCaptor;

    @BeforeEach
    void setUp() {
        // mocks
    }

    @Test
    void testSendPlayerJoinNotification() {
        controllerHelper.sendPlayerJoinNotification(ROOM_ID, PLAYER_NAME);
        verify(messagingTemplate).convertAndSend(destinationCaptor.capture(), payloadCaptor.capture());
        assertEquals("/topic/room/" + ROOM_ID + "/notifications/receive/playerJoined", destinationCaptor.getValue());
        assertEquals(PLAYER_NAME, payloadCaptor.getValue());
    }

    @Test
    void testSendPlayerLeftNotification() {
        controllerHelper.sendPlayerLeftNotification(ROOM_ID, PLAYER_NAME);
        verify(messagingTemplate).convertAndSend(destinationCaptor.capture(), payloadCaptor.capture());
        assertEquals("/topic/room/" + ROOM_ID + "/notifications/receive/playerLeft", destinationCaptor.getValue());
        assertEquals(PLAYER_NAME, payloadCaptor.getValue());
    }

    @Test
    void testSendPlayerGuessedNotification() {
        controllerHelper.sendPlayerGuessedNotification(ROOM_ID, PLAYER_NAME);
        verify(messagingTemplate).convertAndSend(destinationCaptor.capture(), payloadCaptor.capture());
        assertEquals("/topic/room/" + ROOM_ID + "/notifications/receive/playerGuessed", destinationCaptor.getValue());
        assertEquals(PLAYER_NAME, payloadCaptor.getValue());
    }

    @Test
    void testSendGameRoundChangedNotification() {
        controllerHelper.sendGameRoundChangedNotification(ROOM_ID, ROUND_NUMBER);
        verify(messagingTemplate).convertAndSend(destinationCaptor.capture(), payloadCaptor.capture());
        assertEquals("/topic/room/" + ROOM_ID + "/notifications/receive/gameRoundChanged", destinationCaptor.getValue());
        assertEquals(ROUND_NUMBER, payloadCaptor.getValue());
    }

    @Test
    void testSendSessionFinishedNotification() {
        controllerHelper.sendSessionFinishedNotification(ROOM_ID);
        verify(messagingTemplate).convertAndSend(destinationCaptor.capture(), payloadCaptor.capture());
        assertEquals("/topic/room/" + ROOM_ID + "/notifications/receive/gameFinished", destinationCaptor.getValue());
        assertEquals("", payloadCaptor.getValue());
    }

    @Test
    void testSendRoundFinishedNotification() {
        controllerHelper.sendRoundFinishedNotification(ROOM_ID, ROUND_NUMBER);
        verify(messagingTemplate).convertAndSend(destinationCaptor.capture(), payloadCaptor.capture());
        assertEquals("/topic/room/" + ROOM_ID + "/notifications/receive/roundFinished", destinationCaptor.getValue());
        assertEquals(ROUND_NUMBER, payloadCaptor.getValue());
    }

    @Test
    void testSendTheWordWasNotification() {
        controllerHelper.sendTheWordWasNotification(ROOM_ID, WORD);
        verify(messagingTemplate).convertAndSend(destinationCaptor.capture(), payloadCaptor.capture());
        assertEquals("/topic/room/" + ROOM_ID + "/notifications/receive/wordWas", destinationCaptor.getValue());
        assertEquals(WORD, payloadCaptor.getValue());
    }

    @Test
    void testSendGameStateNotification() {
        controllerHelper.sendGameSateNotification(ROOM_ID, GAME_STATE);
        verify(messagingTemplate).convertAndSend(destinationCaptor.capture(), payloadCaptor.capture());
        assertEquals("/topic/room/" + ROOM_ID + "/notifications/receive/gameState", destinationCaptor.getValue());
        assertEquals(GAME_STATE, payloadCaptor.getValue());
    }
}