package com.example.drawling.business.manager;

import com.example.drawling.application.controller.helper.*;
import com.example.drawling.business.interfaces.service.game.GameSessionService;
import com.example.drawling.domain.enums.game.GameSessionState;
import com.example.drawling.domain.model.game.GameSession;
import com.example.drawling.domain.model.game.round.GameRound;
import com.example.drawling.exception.InvalidGameSessionException;
import com.example.drawling.statics.GameStaticSessions;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GameSessionManager {
    private final GameSessionService gameSessionService;
    private final PlayerControllerHelper playerControllerHelper;
    private final GameWordControllerHelper gameWordControllerHelper;
    private final GameNotificationsControllerHelper gameNotificationsControllerHelper;
    private final DrawingCanvasControllerHelper drawingCanvasControllerHelper;
    private final GameRoundControllerHelper gameRoundControllerHelper;
    private final TimerControllerHelper timerControllerHelper;

    public GameSessionManager(GameSessionService gameSessionService, PlayerControllerHelper playerControllerHelper, GameWordControllerHelper gameWordControllerHelper, GameNotificationsControllerHelper gameNotificationsControllerHelper, DrawingCanvasControllerHelper drawingCanvasControllerHelper, GameRoundControllerHelper gameRoundControllerHelper, TimerControllerHelper timerControllerHelper) {
        this.gameSessionService = gameSessionService;
        this.playerControllerHelper = playerControllerHelper;
        this.gameWordControllerHelper = gameWordControllerHelper;
        this.gameNotificationsControllerHelper = gameNotificationsControllerHelper;
        this.drawingCanvasControllerHelper = drawingCanvasControllerHelper;
        this.gameRoundControllerHelper = gameRoundControllerHelper;
        this.timerControllerHelper = timerControllerHelper;
    }


    public void checkGameStart(String link) {
        if (link == null) {
            throw new InvalidGameSessionException("Link cannot be null.");
        }

        GameSession gameSession = GameStaticSessions.getReadOnlySessions().get(link);
        if (gameSession == null) {
            throw new InvalidGameSessionException("No game session found for the provided link.");
        }

        if (gameSession.getGameSessionState() != GameSessionState.STANDBY) {
            throw new InvalidGameSessionException("Game session is not in standby state.");
        }

        if (gameSession.getPlayers().size() < 2) {
            throw new InvalidGameSessionException("Not enough players to start the game session.");
        }

        startGameSession(gameSession);

    }

    public void startGameSession(GameSession gameSession) throws InvalidGameSessionException {
        if (gameSession.getGameMode().getGameRounds().isEmpty()) {
            throw new InvalidGameSessionException("No game rounds found for the game session.");
        }

        long startTime = System.currentTimeMillis() + 10000L;
        for (GameRound gameRound : gameSession.getGameMode().getGameRounds()) {
            gameRound.setStartTime(startTime);
            gameRound.setEndTime(startTime + gameRound.getDuration() * 1000L);
            startTime = gameRound.getEndTime() + 10000L;
        }

        gameSession.setGameSessionState(GameSessionState.ACTIVE);

        // prepare to start 1st round
        Thread.ofVirtual().start(() -> {
            try {
                for (int i = 10; i > 0; i--) {
                    timerControllerHelper.sendToRoom(gameSession.getSessionId(), i + " seconds to start!");
                    Thread.sleep(1000);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        Thread gameLogicThread = new Thread(() -> manageGameLogic(gameSession));
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        gameSession.getGameMode().getGameRounds().getFirst().setActive(true);
        gameLogicThread.start();

        gameRoundControllerHelper.sendRoundNumberForRoom(gameSession.getSessionId(), 1);
        gameWordControllerHelper.sendWordToPlayers(gameSession, gameSession.getGameMode().getGameRounds().getFirst().getWord());
        drawingCanvasControllerHelper.sendCanvasClear(gameSession.getSessionId());
    }


    public void checkRounds(GameSession gameSession) {
        List<GameRound> gameRounds = gameSession.getGameMode().getGameRounds();
        for (int i = 0; i < gameRounds.size(); i++) {
            GameRound gameRound = gameRounds.get(i);
            if (gameRound.isActive()) {
                long currentTime = System.currentTimeMillis();
                if (currentTime >= gameRound.getEndTime()) {
                    gameRound.setActive(false);
                    if (i + 1 < gameRounds.size()) {
                        GameRound nextRound = gameRounds.get(i + 1);
                        nextRound.setActive(true);
                        gameRoundControllerHelper.sendRoundNumberForRoom(gameSession.getSessionId(), i + 2);

                        gameSessionService.cyclePlayers(gameSession);
                        playerControllerHelper.sendPlayerRoles(gameSession);
                        gameWordControllerHelper.sendWordToPlayers(gameSession, nextRound.getWord());

                        gameNotificationsControllerHelper.sendGameSateNotification(gameSession.getSessionId(), GameSessionState.ROUND_ACTIVE);
                        drawingCanvasControllerHelper.sendCanvasClear(gameSession.getSessionId());

                    } else {
                        gameSession.setGameSessionState(GameSessionState.ENDED);
                        gameNotificationsControllerHelper.sendGameSateNotification(gameSession.getSessionId(), GameSessionState.ENDED);
                        gameNotificationsControllerHelper.sendSessionFinishedNotification(gameSession.getSessionId());
                    }
                    playerControllerHelper.sendActivePlayers(gameSession.getSessionId());

                    break;
                }
            }
        }
    }


    private void manageGameLogic(GameSession gameSession) {
        boolean roundActive;
        List<GameRound> gameRounds = gameSession.getGameMode().getGameRounds();

        do {
            roundActive = false;

            for (GameRound gameRound : gameRounds) {
                if (gameRound.isActive()) {
                    roundActive = true;
                    if (isRoundFinished(gameRound)) {
                        handleRoundFinished(gameSession, gameRound);
                    } else {
                        long timeRemaining = gameRound.getEndTime() - System.currentTimeMillis();
                        timerControllerHelper.sendToRoom(gameSession.getSessionId(), String.valueOf(timeRemaining / 1000) + "s");
                    }
                }
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        } while (roundActive);
    }

    private boolean isRoundFinished(GameRound gameRound) {
        return System.currentTimeMillis() >= gameRound.getEndTime();
    }

    private void handleRoundFinished(GameSession gameSession, GameRound gameRound) {
        gameNotificationsControllerHelper.sendTheWordWasNotification(gameSession.getSessionId(), gameRound.getWord());
        gameNotificationsControllerHelper.sendGameSateNotification(gameSession.getSessionId(), GameSessionState.ROUND_FINISHED);
        gameSession.getGameMode().getGameRounds().getFirst().getCanvasEvents().clear(); // clear drawings

        Thread counterThread = new Thread(() -> {
            for (int i = 10; i > 0; i--) {
                timerControllerHelper.sendToRoom(gameSession.getSessionId(), "Next round starts in: " + i);

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
        counterThread.start();

        try {
            counterThread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        checkRounds(gameSession);
    }
}