package com.example.drawling.application.controller.game;

import com.example.drawling.application.controller.helper.GameNotificationsControllerHelper;
import com.example.drawling.application.controller.helper.PlayerControllerHelper;
import com.example.drawling.business.interfaces.service.game.GameSessionService;
import com.example.drawling.domain.model.game.GameSession;
import com.example.drawling.statics.GameStaticSessions;
import org.springframework.context.annotation.Scope;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@Scope("singleton")
public class WebSocketEventListener {
    private static List<String> sessionRegistry;
    private final GameSessionService gameSessionService;
    private final PlayerControllerHelper playerControllerHelper;
    private final GameNotificationsControllerHelper gameNotificationsControllerHelper;

    public WebSocketEventListener(GameSessionService gameSessionService, PlayerControllerHelper playerControllerHelper, GameNotificationsControllerHelper gameNotificationsControllerHelper) {
        this.gameSessionService = gameSessionService;
        this.playerControllerHelper = playerControllerHelper;
        this.gameNotificationsControllerHelper = gameNotificationsControllerHelper;
        sessionRegistry = new ArrayList<>();
    }

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectEvent event) {
        String sessionId = Objects.requireNonNull(event.getMessage().getHeaders().get("simpSessionId")).toString();
        sessionRegistry.add(sessionId);
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        GameSession gs = gameSessionService.getGameSessionByPlayerSessionId(event.getSessionId());
        if (gs == null) {
            return;
        }

        gameNotificationsControllerHelper.sendPlayerLeftNotification(gs.getSessionId(), gs.getPlayers().get(event.getSessionId()).getPlayer().getDisplayName());
        sessionRegistry.remove(event.getSessionId());
        String sessionId = event.getSessionId();
        Map<String, GameSession> gameSessionMap = GameStaticSessions.getReadOnlySessions();

        String gameSessionId = null;
        for (Map.Entry<String, GameSession> entry : gameSessionMap.entrySet()) {
            if (entry.getValue().getPlayers().containsKey(sessionId)) {
                gameSessionId = entry.getKey();
                break;
            }
        }

        if (gameSessionId != null) {
            GameSession gameSession = gameSessionMap.get(gameSessionId);
            boolean kicked = gameSessionService.kickPlayer(gameSession, sessionId);

            if (kicked) {
                playerControllerHelper.sendActivePlayers(gameSessionId);
                playerControllerHelper.sendPlayerRoles(gameSession);
            }
        }
    }
}