package com.example.drawling.application.controller.game;

import com.example.drawling.application.controller.helper.GameNotificationsControllerHelper;
import com.example.drawling.application.controller.helper.PlayerControllerHelper;
import com.example.drawling.application.handler.PlayerHandler;
import com.example.drawling.business.interfaces.service.game.GameSessionService;
import com.example.drawling.business.manager.GameSessionManager;
import com.example.drawling.domain.dto.game.ActivePlayerDTO;
import com.example.drawling.domain.dto.game.PlayerJoinRequestDTO;
import com.example.drawling.domain.enums.game.GamePlayerRole;
import com.example.drawling.domain.model.Player;
import com.example.drawling.domain.model.game.GameSession;
import com.example.drawling.domain.model.game.PlayerSession;
import com.example.drawling.statics.GameStaticSessions;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RestController
@SuppressWarnings("unchecked")
public class PlayerController {
    private static final String SEND_SESSION_TOPIC = "/topic/sendSession";

    private final SimpMessagingTemplate messagingTemplate;
    private final GameSessionManager gameSessionManager;
    private final GameSessionService gameSessionService;
    private final GameNotificationsControllerHelper gameNotificationsControllerHelper;
    private final PlayerControllerHelper playerControllerHelper;
    private final PlayerHandler playerHandler;

    public PlayerController(SimpMessagingTemplate messagingTemplate, GameSessionManager gameSessionManager, GameSessionService gameSessionService, GameNotificationsControllerHelper gameNotificationsControllerHelper, PlayerControllerHelper playerControllerHelper, PlayerHandler playerHandler) {
        this.messagingTemplate = messagingTemplate;
        this.gameSessionManager = gameSessionManager;
        this.gameSessionService = gameSessionService;
        this.gameNotificationsControllerHelper = gameNotificationsControllerHelper;
        this.playerControllerHelper = playerControllerHelper;
        this.playerHandler = playerHandler;
    }


    @GetMapping("/api/room/full/{roomId}")
    public ResponseEntity<Void> isRoomFull(@PathVariable String roomId) {
        GameSession gameSession = GameStaticSessions.getByLink(roomId);
        if (gameSession == null) {
            return ResponseEntity.notFound().build();
        }

        boolean isFull = gameSessionService.isMaxPlayersReached(gameSession);
        if (isFull) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } else {
            return ResponseEntity.ok().build();
        }
    }

    @GetMapping("/api/room/players/{roomId}")
    public ResponseEntity<List<ActivePlayerDTO>> getActivePlayersInRoom(@PathVariable String roomId) {
        GameSession gameSession = GameStaticSessions.getByLink(roomId);
        if (gameSession == null) {
            return ResponseEntity.notFound().build();
        }

        List<PlayerSession> allPlayers = gameSessionService.getAllPlayers(gameSession);
        List<ActivePlayerDTO> activePlayerDTOs = new ArrayList<>();

        for (PlayerSession playerSession : allPlayers) {
            ActivePlayerDTO activePlayerDTO = new ActivePlayerDTO(playerSession.getPlayer().getDisplayName(), playerSession.getPlayer().getBalance(), playerSession.getRole(), playerSession.getPlayer().getProfilePictureUrl());
            activePlayerDTOs.add(activePlayerDTO);
        }

        return ResponseEntity.ok(activePlayerDTOs);
    }

    @GetMapping("/api/room/{roomId}/check-display-name")
    public ResponseEntity<String> checkDisplayNameTaken(@PathVariable String roomId, @RequestParam String displayName) {
        GameSession gameSession = GameStaticSessions.getByLink(roomId);

        if (gameSession == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(playerHandler.handleCheckPlayerNameDuplication(roomId, displayName));
    }

    @GetMapping("/api/rooms/{roomId}/users/{userId}")
    public ResponseEntity<Void> isUserInRoom(@PathVariable String roomId, @PathVariable int userId) {
        GameSession gameSession = GameStaticSessions.getByLink(roomId);
        if (gameSession == null) {
            return ResponseEntity.notFound().build();
        }

        for (PlayerSession playerSession : gameSession.getPlayers().values()) {
            if (playerSession.getPlayer().getId() == userId) {
                return ResponseEntity.badRequest().build();
            }
        }

        return ResponseEntity.ok().build();
    }




    @MessageMapping("/player/join/{roomId}")
    public void playerJoin(@DestinationVariable String roomId, PlayerJoinRequestDTO playerJoinRequestDTO, SimpMessageHeaderAccessor headerAccessor) {
        GameSession gameSession = GameStaticSessions.getByLink(roomId);
        if (gameSession == null) {
            return;
        }

        String playerName = playerJoinRequestDTO.getDisplayName();

        if (gameSessionService.isMaxPlayersReached(gameSession)) {
            messagingTemplate.convertAndSendToUser(playerName, SEND_SESSION_TOPIC, "FULL");
            return;
        }

        String sessionId = headerAccessor.getSessionId();
        Integer userId = playerJoinRequestDTO.getUserId(); // because can be null (for not registered players)

        Player player;

        if (userId == null) {
            // not logged in
            player = new Player(playerName);
        } else {
            // logged in
            player = new Player(userId, playerName);
        }

        PlayerSession playerSession = new PlayerSession(sessionId, player);


        playerHandler.handlePlayerJoinStats(playerSession); // if logged in, sets player stats from db
        gameSessionService.addPlayer(gameSession, playerSession);

        // send the sessionId to the client
        messagingTemplate.convertAndSendToUser(playerName, SEND_SESSION_TOPIC, Objects.requireNonNull(headerAccessor.getSessionId()));
        gameNotificationsControllerHelper.sendPlayerJoinNotification(roomId, playerSession.getPlayer().getDisplayName());

        gameSessionManager.checkGameStart(roomId);
    }

    @MessageMapping("room/send/getPlayers/{roomId}")
    @SendTo("/topic/room/receive/activePlayers/{roomId}")
    public void getPlayers(@DestinationVariable String roomId) {
        playerControllerHelper.sendActivePlayers(roomId);
    }


    @MessageMapping("room/{roomId}/send/getPlayerRole")
    @SendToUser("/topic/player/role")
    public GamePlayerRole getPlayerRole(@DestinationVariable String roomId, SimpMessageHeaderAccessor headerAccessor) {
        String sessionId = headerAccessor.getSessionId();
        GameSession gameSession = GameStaticSessions.getByLink(roomId);
        PlayerSession playerSession = gameSessionService.getPlayerBySessionAndPlayerSessionId(gameSession, sessionId);

        return playerSession.getRole();
    }

}
