package com.example.drawling.application.controller.helper;

import com.example.drawling.business.interfaces.service.game.GameSessionService;
import com.example.drawling.domain.dto.game.ActivePlayerDTO;
import com.example.drawling.domain.enums.game.GamePlayerRole;
import com.example.drawling.domain.model.game.GameSession;
import com.example.drawling.domain.model.game.PlayerSession;
import com.example.drawling.statics.GameStaticSessions;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PlayerControllerHelper {
    private final GameSessionService gameSessionService;
    private final SimpMessagingTemplate messagingTemplate;

    public PlayerControllerHelper(GameSessionService gameSessionService, SimpMessagingTemplate messagingTemplate) {
        this.gameSessionService = gameSessionService;
        this.messagingTemplate = messagingTemplate;
    }

    private List<ActivePlayerDTO> getActivePlayersDTO(String roomId) {
        List<PlayerSession> allPlayers = gameSessionService.getAllPlayers(GameStaticSessions.getByLink(roomId));
        return allPlayers.stream()
                .map(playerSession -> new ActivePlayerDTO(playerSession.getPlayer().getDisplayName(), playerSession.getPlayer().getBalance(), playerSession.getRole(), playerSession.getPlayer().getProfilePictureUrl()))
                .toList();
    }

    public void sendActivePlayers(String roomId) {
        List<ActivePlayerDTO> activePlayers = getActivePlayersDTO(roomId);
        messagingTemplate.convertAndSend("/topic/room/receive/activePlayers/" + roomId, activePlayers);
    }

    public void sendPlayerRoles(GameSession gameSession) {
        List<PlayerSession> playerSessions = gameSessionService.getAllPlayers(gameSession);
        for (PlayerSession playerSession : playerSessions) {
            String sessionId = playerSession.getSessionId();
            GamePlayerRole role = playerSession.getRole();
            messagingTemplate.convertAndSendToUser(sessionId, "/topic/player/role", role);
        }
    }
}
