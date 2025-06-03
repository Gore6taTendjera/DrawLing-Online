package com.example.drawling.application.controller.helper;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class GameRoundControllerHelper {
    private final SimpMessagingTemplate simpMessagingTemplate;

    public GameRoundControllerHelper(SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
    }


    public void sendRoundNumberForRoom(String roomId, int roundNumber) {
        simpMessagingTemplate.convertAndSend("/topic/room/" + roomId + "/receive/roundNumber", roundNumber);
    }

}
