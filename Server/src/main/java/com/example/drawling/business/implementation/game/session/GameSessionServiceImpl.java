package com.example.drawling.business.implementation.game.session;

import com.example.drawling.business.helper.UniqueIdGeneratorHelper;
import com.example.drawling.business.interfaces.service.game.GameSessionService;
import com.example.drawling.domain.enums.game.GamePlayerRole;
import com.example.drawling.domain.model.game.GameSession;
import com.example.drawling.domain.model.game.PlayerSession;
import com.example.drawling.domain.model.game.mode.GameMode;
import com.example.drawling.domain.model.game.round.GameRound;
import com.example.drawling.exception.GameSessionNotFoundException;
import com.example.drawling.exception.InsufficientPlayersException;
import com.example.drawling.exception.InvalidGameSessionException;
import com.example.drawling.exception.PlayerSessionNotFoundException;
import com.example.drawling.statics.GameStaticSessions;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@Service
@SuppressWarnings("unchecked")
public class GameSessionServiceImpl implements GameSessionService {
    private final UniqueIdGeneratorHelper uniqueIdGenerator;

    public GameSessionServiceImpl(UniqueIdGeneratorHelper uniqueIdGenerator) {
        this.uniqueIdGenerator = uniqueIdGenerator;
    }

    public String addGameSession(GameSession gameSession) {
        String uniqueId = uniqueIdGenerator.generateUniqueId();
        gameSession.setSessionId(uniqueId);
        GameStaticSessions.addSession(uniqueId, gameSession);

        return uniqueId;
    }

    public GameSession getGameSessionByPlayerSessionId(String playerSessionId) {
        return GameStaticSessions.getReadOnlySessions().values().stream()
                .filter(gameSession -> gameSession.getPlayers().containsKey(playerSessionId))
                .findFirst()
                .orElseThrow(() -> new PlayerSessionNotFoundException("Game session not found for player session ID: " + playerSessionId));
    }

    public GameSession getGameSessionById(String sessionId) {
        try {
            return GameStaticSessions.getByLink(sessionId);
        } catch (NoSuchElementException e) {
            throw new GameSessionNotFoundException("Game session not found for session ID: " + sessionId, e);
        }
    }

    public boolean addPlayer(GameSession gameSession, PlayerSession playerSession) {
        if (isMaxPlayersReached(gameSession)) {
            throw new InsufficientPlayersException("Maximum players reached for the game session");
        }

        int minDrawingPlayers = gameSession.getGameMode().getMinDrawingPlayers();
        int maxDrawingPlayers = gameSession.getGameMode().getMaxDrawingPlayers();
        int minGuessingPlayers = gameSession.getGameMode().getMinGuessingPlayers();
        int maxGuessingPlayers = gameSession.getGameMode().getMaxGuessingPlayers();

        Map<String, PlayerSession> players = gameSession.getPlayers();

        long drawingCount = players.values().stream()
                .filter(session -> session.getRole() == GamePlayerRole.DRAWING).count();
        long guessingCount = players.values().stream()
                .filter(session -> session.getRole() == GamePlayerRole.GUESSING).count();

        GamePlayerRole assignedRole = null;

        if (drawingCount < minDrawingPlayers) {
            assignedRole = GamePlayerRole.DRAWING;
        } else if (guessingCount < minGuessingPlayers) {
            assignedRole = GamePlayerRole.GUESSING;
        } else if (drawingCount < maxDrawingPlayers) {
            assignedRole = GamePlayerRole.DRAWING;
        } else if (guessingCount < maxGuessingPlayers) {
            assignedRole = GamePlayerRole.GUESSING;
        }

        playerSession.setRole(assignedRole);
        players.put(playerSession.getSessionId(), playerSession);

        return true;
    }

    public boolean isMaxPlayersReached(GameSession gameSession) {
        GameMode gameMode = gameSession.getGameMode();

        return gameSession.getPlayers().size() >= gameMode.getMaxPlayers();
    }

    public PlayerSession getPlayerBySessionAndPlayerSessionId(GameSession gameSession, String sessionId) {
        if (gameSession == null) {
            throw new IllegalArgumentException("Game session cannot be null");
        }
        if (sessionId == null || sessionId.isEmpty()) {
            throw new IllegalArgumentException("Session ID cannot be null or empty");
        }

        return gameSession.getPlayers().get(sessionId);
    }

    public PlayerSession getPlayerBySessionId(String sessionId) {
        if (sessionId == null || sessionId.isEmpty()) {
            throw new IllegalArgumentException("Session ID cannot be null or empty");
        }

        for (GameSession gameSession : GameStaticSessions.getReadOnlySessions().values()) {
            PlayerSession playerSession = gameSession.getPlayers().get(sessionId);
            if (playerSession != null) {
                return playerSession;
            }
        }

        throw new PlayerSessionNotFoundException("Player session not found for session ID: " + sessionId);
    }


    public List<PlayerSession> getAllPlayers(GameSession gameSession) {
        if (gameSession == null || gameSession.getPlayers() == null) {
            throw new InvalidGameSessionException("Game session or players list is null");
        }
        return gameSession.getPlayers().values().stream().toList();
    }


    public GameRound getActiveGameRound(GameSession gameSession) {
        return gameSession.getGameMode().getGameRounds().stream()
                .filter(GameRound::isActive)
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("No active round found in game session: " + gameSession));
    }

    // normal game mode
    public void cyclePlayers(GameSession gameSession) {
        Map<String, PlayerSession> players = gameSession.getPlayers();
        List<PlayerSession> guessingPlayers = players.values().stream()
                .filter(session -> session.getRole() == GamePlayerRole.GUESSING)
                .toList();

        List<PlayerSession> drawingPlayers = players.values().stream()
                .filter(session -> session.getRole() == GamePlayerRole.DRAWING)
                .toList();

        try {
            if (guessingPlayers.isEmpty() || drawingPlayers.isEmpty()) {
                throw new InsufficientPlayersException("Insufficient players in the game session to cycle roles");
            }

            SecureRandom random = new SecureRandom();
            PlayerSession guessingPlayer = guessingPlayers.get(random.nextInt(guessingPlayers.size()));
            PlayerSession drawingPlayer = drawingPlayers.get(random.nextInt(drawingPlayers.size()));

            guessingPlayer.setRole(GamePlayerRole.DRAWING);
            drawingPlayer.setRole(GamePlayerRole.GUESSING);
        } catch (InsufficientPlayersException e) {
            throw new IllegalStateException("Insufficient players in the game session to cycle roles", e);
        }
    }


    public boolean kickPlayer(GameSession gameSession, String playerSession) {
        Map<String, PlayerSession> players = gameSession.getPlayers();
        PlayerSession kickedPlayer = players.get(playerSession);

        if (kickedPlayer == null) {
            throw new IllegalArgumentException("Player session not found: " + playerSession);
        }

        try {
            if (kickedPlayer.getRole() == GamePlayerRole.DRAWING) {
                PlayerSession newDrawingPlayer = players.values().stream()
                        .filter(session -> session.getRole() == GamePlayerRole.GUESSING)
                        .findFirst()
                        .orElseThrow(() -> new NoSuchElementException("No guessing player found to replace the drawing player"));

                newDrawingPlayer.setRole(GamePlayerRole.DRAWING);
            }

            players.remove(playerSession);
            return true;
        } catch (NoSuchElementException e) {
            throw new IllegalStateException("No guessing player found to replace the drawing player", e);
        }
    }
}