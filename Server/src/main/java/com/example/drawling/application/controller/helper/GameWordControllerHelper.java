package com.example.drawling.application.controller.helper;

import com.example.drawling.domain.enums.game.GamePlayerRole;
import com.example.drawling.domain.model.game.GameSession;
import com.example.drawling.domain.model.game.PlayerSession;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
public class GameWordControllerHelper {
    private final SimpMessagingTemplate messagingTemplate;


    public GameWordControllerHelper(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void sendWordToPlayers(GameSession gameSession, String word) {
        for (PlayerSession playerSession : gameSession.getPlayers().values()) {
            if (playerSession.getRole() == GamePlayerRole.DRAWING) {
                messagingTemplate.convertAndSendToUser(playerSession.getSessionId(),
                        "topic/room/receive/word",
                        word);
            } else if (playerSession.getRole() == GamePlayerRole.GUESSING) {
                int wordLength = word.length();
                messagingTemplate.convertAndSendToUser(playerSession.getSessionId(),
                        "topic/room/receive/word",
                        IntStream.range(0, wordLength).mapToObj(i -> "_").collect(Collectors.joining(" ")));
            }
        }
    }

}
