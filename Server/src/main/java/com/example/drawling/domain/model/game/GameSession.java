package com.example.drawling.domain.model.game;

import com.example.drawling.domain.enums.game.GameSessionState;
import com.example.drawling.domain.model.game.mode.GameMode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class GameSession {
    private String sessionId;
    private GameMode gameMode;
    private GameSessionState gameSessionState;
    private int currentRoundIndex;
    private Map<String, PlayerSession> players;

    public GameSession(GameMode gameMode, GameSessionState gameSessionState) {
        this.gameMode = gameMode;
        this.gameSessionState = gameSessionState;
        this.currentRoundIndex = 0;
        this.players = new HashMap<>();
    }
}
