package com.example.drawling.application.controller.game;

import com.example.drawling.application.controller.helper.GameNotificationsControllerHelper;
import com.example.drawling.business.interfaces.service.game.GameSessionService;
import com.example.drawling.domain.enums.game.GamePlayerRole;
import com.example.drawling.domain.model.game.GameSession;
import com.example.drawling.domain.model.game.PlayerSession;
import com.example.drawling.domain.model.game.round.GameRound;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RestController
public class GameWordController {
    private final GameSessionService gameSessionService;
    private final GameNotificationsControllerHelper gameNotificationsControllerHelper;

    public GameWordController(GameSessionService gameSessionService, GameNotificationsControllerHelper gameNotificationsControllerHelper) {
        this.gameSessionService = gameSessionService;
        this.gameNotificationsControllerHelper = gameNotificationsControllerHelper;
    }


    @MessageMapping("/room/send/word")
    @SendToUser("topic/room/receive/word")
    public String sendWord(SimpMessageHeaderAccessor headerAccessor) {
        String playerId = headerAccessor.getSessionId();

        PlayerSession playerSession = gameSessionService.getPlayerBySessionId(playerId);
        GamePlayerRole role = playerSession.getRole();

        GameSession gameSession = gameSessionService.getGameSessionByPlayerSessionId(playerId);
        GameRound activeGameRound = gameSessionService.getActiveGameRound(gameSession);

        if (role == GamePlayerRole.DRAWING) {
            return activeGameRound.getWord();
        } else if (role == GamePlayerRole.GUESSING) {
            return IntStream.range(0, activeGameRound.getWord().length()).mapToObj(i -> "_").collect(Collectors.joining(" "));
        }
        return null;
    }

    @MessageMapping("/room/send/word/guess")
    @SendToUser("topic/room/receive/word/guess")
    public boolean wordGuessing(SimpMessageHeaderAccessor headerAccessor, String word) {
        String playerId = headerAccessor.getSessionId();
        PlayerSession playerSession = gameSessionService.getPlayerBySessionId(playerId);
        GamePlayerRole role = playerSession.getRole();

        GameSession gameSession = gameSessionService.getGameSessionByPlayerSessionId(playerId);
        GameRound activeGameRound = gameSessionService.getActiveGameRound(gameSession);
        if (role == GamePlayerRole.GUESSING) {
            boolean guessed = word.equalsIgnoreCase(activeGameRound.getWord());
            if (guessed) {
                gameNotificationsControllerHelper.sendPlayerGuessedNotification(gameSession.getSessionId(), playerSession.getPlayer().getDisplayName());
                return true;
            }
            return word.equalsIgnoreCase(activeGameRound.getWord());
        }
        return false;
    }
}
