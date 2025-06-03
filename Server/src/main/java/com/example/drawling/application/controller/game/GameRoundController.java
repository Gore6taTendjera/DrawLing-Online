package com.example.drawling.application.controller.game;

import com.example.drawling.business.interfaces.service.game.GameSessionService;
import com.example.drawling.domain.model.game.GameSession;
import com.example.drawling.domain.model.game.round.GameRound;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GameRoundController {
    private final GameSessionService gameSessionService;

    public GameRoundController(GameSessionService gameSessionService) {
        this.gameSessionService = gameSessionService;
    }


    @MessageMapping("/room/send/roundNumber")
    @SendToUser("topic/room/receive/roundNumber")
    public int getRoundNumberForRoom(SimpMessageHeaderAccessor headerAccessor) {
        String playerId = headerAccessor.getSessionId();

        GameSession gameSession = gameSessionService.getGameSessionByPlayerSessionId(playerId);
        GameRound activeGameRound = gameSessionService.getActiveGameRound(gameSession);

        int index = gameSession.getGameMode().getGameRounds().indexOf(activeGameRound);
        return index + 1;
    }



}
