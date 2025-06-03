package com.example.drawling.application.controller.helper;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class TimerControllerHelper {
    private final SimpMessagingTemplate messagingTemplate;

    public TimerControllerHelper(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void sendToRoom(String roomId, String time) {
        messagingTemplate.convertAndSend("/topic/timer/" + roomId, time);
    }
}
