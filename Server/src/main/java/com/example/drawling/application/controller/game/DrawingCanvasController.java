package com.example.drawling.application.controller.game;

import com.example.drawling.application.controller.helper.DrawingCanvasControllerHelper;
import com.example.drawling.application.handler.PlayerHandler;
import com.example.drawling.business.interfaces.service.game.GameSessionService;
import com.example.drawling.domain.model.game.GameSession;
import com.example.drawling.domain.model.game.PlayerSession;
import com.example.drawling.domain.model.game.canvas.CanvasClear;
import com.example.drawling.domain.model.game.canvas.CanvasDrawLine;
import com.example.drawling.domain.model.game.canvas.CanvasEvent;
import com.example.drawling.domain.model.game.round.GameRound;
import com.example.drawling.statics.GameStaticSessions;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;


@RestController
public class DrawingCanvasController {

    private final SimpMessagingTemplate messagingTemplate;
    private final GameSessionService gameSessionService;
    private final PlayerHandler playerHandler;
    private final DrawingCanvasControllerHelper drawingCanvasControllerHelper;

    public DrawingCanvasController(SimpMessagingTemplate messagingTemplate, GameSessionService gameSessionService, PlayerHandler playerHandler, DrawingCanvasControllerHelper drawingCanvasControllerHelper) {
        this.messagingTemplate = messagingTemplate;
        this.gameSessionService = gameSessionService;
        this.playerHandler = playerHandler;
        this.drawingCanvasControllerHelper = drawingCanvasControllerHelper;
    }

    // canvas draw real time
    @MessageMapping("/canvas/send/draw")
    public void receiveCanvasData(CanvasDrawLine canvasDrawLine, SimpMessageHeaderAccessor headerAccessor) {
        String currentSessionId = headerAccessor.getSessionId();
        GameSession gs = gameSessionService.getGameSessionByPlayerSessionId(currentSessionId);

        gs.getGameMode().getGameRounds().getFirst().getCanvasEvents().add(canvasDrawLine);

        List<PlayerSession> playerSessions = gameSessionService.getAllPlayers(gs);
        for (PlayerSession playerSession : playerSessions) {
            if (!playerSession.getSessionId().equals(currentSessionId)) {
                messagingTemplate.convertAndSendToUser(playerSession.getSessionId(), "/topic/canvas/receive/draw", canvasDrawLine);
            }
        }
    }

    // canvas clear real time
    @MessageMapping("/canvas/send/clear/{roomId}")
    @SendTo("/topic/canvas/receive/clear/{roomId}")
    public String clearCanvas(@DestinationVariable String roomId) {
        CanvasClear canvasClear = new CanvasClear();
        GameSession gameSession = GameStaticSessions.getByLink(roomId);
        GameRound gameRound = gameSession.getGameMode().getGameRounds().getFirst();

        List<CanvasEvent> canvasEvents = gameRound.getCanvasEvents();
        int index = canvasEvents.size();
        canvasEvents.subList(0, index).clear();
        canvasEvents.add(canvasClear);
        return "";
    }


    // on load
    @MessageMapping("/canvas/send/saved")
    public void receiveSavedCanvasData(SimpMessageHeaderAccessor headerAccessor) {
        String playerId = headerAccessor.getSessionId();
        String roomId = gameSessionService.getGameSessionByPlayerSessionId(playerId).getSessionId();

        List<CanvasEvent> canvasEvents;
        synchronized (GameStaticSessions.getByLink(roomId)) {
            canvasEvents = new ArrayList<>(GameStaticSessions.getByLink(roomId).getGameMode().getGameRounds().getFirst().getCanvasEvents());
        }

        for (CanvasEvent canvasEvent : canvasEvents) {
            try {
                Thread.sleep(2);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
            if (canvasEvent instanceof CanvasDrawLine canvasDrawLine) {
                drawingCanvasControllerHelper.sendSavedLines(playerId, canvasDrawLine);
            }
        }
    }

    @MessageMapping("/canvas/send/saveImage")
    public void saveCanvasImage(SimpMessageHeaderAccessor headerAccessor) {
        String playerSessionId = headerAccessor.getSessionId();
        playerHandler.handlePlayerSaveImage(playerSessionId);

    }




}