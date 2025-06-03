package com.example.drawling.business.interfaces.service.game;

import com.example.drawling.domain.model.game.GameSession;
import com.example.drawling.domain.model.game.PlayerSession;
import com.example.drawling.domain.model.game.round.GameRound;

import java.util.List;

public interface GameSessionService {

    boolean addPlayer(GameSession gameSession, PlayerSession playerSession);
    String addGameSession(GameSession gameSession);

    GameSession getGameSessionByPlayerSessionId(String playerSessionId);

    GameSession getGameSessionById(String sessionId);

    PlayerSession getPlayerBySessionAndPlayerSessionId(GameSession gameSession, String sessionId);
    PlayerSession getPlayerBySessionId(String sessionId);
    List<PlayerSession> getAllPlayers(GameSession gameSession);

    GameRound getActiveGameRound(GameSession gameSession);

    boolean isMaxPlayersReached(GameSession gameSession);

    void cyclePlayers(GameSession gameSession);

    boolean kickPlayer(GameSession gameSession, String playerSession);

}