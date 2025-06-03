package com.example.drawling.application.controller.game;

import com.example.drawling.application.controller.helper.PlayerControllerHelper;
import com.example.drawling.business.implementation.game.session.GameSessionServiceImpl;
import com.example.drawling.domain.model.game.GameSession;
import com.example.drawling.domain.model.game.PlayerSession;
import com.example.drawling.statics.GameStaticSessions;
import lombok.Getter;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

import java.util.HashMap;
import java.util.Map;

@Controller
@EnableScheduling
@Getter // bcs of unit tests
public class HeartbeatMonitorController {
    private final GameSessionServiceImpl gameSessionService;
    private final SimpMessagingTemplate messagingTemplate;
    private final PlayerControllerHelper playerControllerHelper;

    private final Map<String, Long> lastActiveTimes = new HashMap<>();

    public HeartbeatMonitorController(GameSessionServiceImpl gameSessionService, SimpMessagingTemplate messagingTemplate, PlayerControllerHelper playerControllerHelper) {
        this.gameSessionService = gameSessionService;
        this.messagingTemplate = messagingTemplate;
        this.playerControllerHelper = playerControllerHelper;
    }

    public void updateLastActiveTime(String sessionId) {
        lastActiveTimes.put(sessionId, System.currentTimeMillis());
    }


    @MessageMapping("/player/heartbeat/{roomId}")
    public void playerHeartbeat(@DestinationVariable String roomId, SimpMessageHeaderAccessor headerAccessor) {
        String playerSessionId = headerAccessor.getSessionId();
        GameSession gameSession = GameStaticSessions.getByLink(roomId);
        PlayerSession playerSession = gameSessionService.getPlayerBySessionAndPlayerSessionId(gameSession, playerSessionId);

        if (playerSession != null) {
            updateLastActiveTime(playerSessionId);
        }
    }

    @Scheduled(fixedDelay = 5000)
    public void checkInactivePlayers() {
        long currentTime = System.currentTimeMillis();
        long timeout = 1000000000;

        for (Map.Entry<String, GameSession> entry : GameStaticSessions.getReadOnlySessions().entrySet()) {
            String roomId = entry.getKey();
            GameSession gameSession = entry.getValue();

            for (PlayerSession playerSession : gameSessionService.getAllPlayers(gameSession)) {
                if (playerSession == null) {
                    continue;
                }

                Long lastActiveTime = lastActiveTimes.get(playerSession.getSessionId());
                if (lastActiveTime != null && currentTime - lastActiveTime > timeout) {
                    gameSessionService.kickPlayer(gameSession, playerSession.getSessionId());
                    messagingTemplate.convertAndSendToUser(playerSession.getSessionId(), "/topic/room/" + roomId + "/kicked", "you are kicked");
                    playerControllerHelper.sendActivePlayers(roomId);
                }
            }
        }
    }
}