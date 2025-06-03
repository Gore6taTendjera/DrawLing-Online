package com.example.drawling.application.controller.helper;

import com.example.drawling.domain.model.game.canvas.CanvasDrawLine;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DrawingCanvasControllerHelper {
    private  final SimpMessagingTemplate simpMessagingTemplate;

    public DrawingCanvasControllerHelper(SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    public void sendCanvasClear(String roomId) {
        if (roomId == null) {
            throw new NullPointerException("roomId cannot be null");
        }
        simpMessagingTemplate.convertAndSend("/topic/canvas/receive/clear/" + roomId, "");
    }


    public void sendSavedLines(String playerSession, CanvasDrawLine canvasDrawLine) {
        simpMessagingTemplate.convertAndSendToUser(playerSession, "/topic/canvas/receive/saved", canvasDrawLine);
    }

}
