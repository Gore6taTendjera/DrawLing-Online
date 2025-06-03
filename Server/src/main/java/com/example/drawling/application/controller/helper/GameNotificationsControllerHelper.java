package com.example.drawling.application.controller.helper;

import com.example.drawling.domain.enums.game.GameSessionState;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GameNotificationsControllerHelper {
    private final SimpMessagingTemplate messagingTemplate;

    private static final String TOPIC_PREFIX = "/topic/room/";
    private static final String NOTIFICATIONS_RECEIVE = "/notifications/receive/";

    public GameNotificationsControllerHelper(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void sendPlayerJoinNotification(String roomId, String playerName) {
        messagingTemplate.convertAndSend(TOPIC_PREFIX + roomId + NOTIFICATIONS_RECEIVE + "playerJoined", playerName);
    }

    public void sendPlayerLeftNotification(String roomId, String playerName) {
        messagingTemplate.convertAndSend(TOPIC_PREFIX + roomId + NOTIFICATIONS_RECEIVE + "playerLeft", playerName);
    }

    public void sendPlayerGuessedNotification(String roomId, String playerName) {
        messagingTemplate.convertAndSend(TOPIC_PREFIX + roomId + NOTIFICATIONS_RECEIVE + "playerGuessed", playerName);
    }

    public void sendGameRoundChangedNotification(String roomId, int roundNumber) {
        messagingTemplate.convertAndSend(TOPIC_PREFIX + roomId + NOTIFICATIONS_RECEIVE + "gameRoundChanged", roundNumber);
    }

    public void sendSessionFinishedNotification(String roomId) {
        messagingTemplate.convertAndSend(TOPIC_PREFIX + roomId + NOTIFICATIONS_RECEIVE + "gameFinished", "");
    }

    public void sendRoundFinishedNotification(String roomId, int roundNumber) {
        messagingTemplate.convertAndSend(TOPIC_PREFIX + roomId + NOTIFICATIONS_RECEIVE + "roundFinished", roundNumber);
    }

    public void sendTheWordWasNotification(String roomId, String word) {
        messagingTemplate.convertAndSend(TOPIC_PREFIX + roomId + NOTIFICATIONS_RECEIVE + "wordWas", word);
    }

    public void sendGameSateNotification(String roomId, GameSessionState state) {
        messagingTemplate.convertAndSend(TOPIC_PREFIX + roomId + NOTIFICATIONS_RECEIVE + "gameState", state);
    }

}
