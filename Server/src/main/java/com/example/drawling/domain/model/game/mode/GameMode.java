package com.example.drawling.domain.model.game.mode;

import com.example.drawling.domain.enums.game.GameModeState;
import com.example.drawling.domain.model.game.round.GameRound;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public abstract class GameMode {
    private int maxPlayers;
    private int minDrawingPlayers;
    private int maxDrawingPlayers;
    private int minGuessingPlayers;
    private int maxGuessingPlayers;
    private List<GameRound> gameRounds;
    private GameModeState gameModeState;

    protected GameMode(int maxPlayers, List<GameRound> gameRounds, GameModeState gameModeState) {
        this.maxPlayers = maxPlayers;
        this.gameRounds = gameRounds;
        this.gameModeState = gameModeState;
    }

    protected GameMode(int maxPlayers, List<GameRound> gameRounds, GameModeState gameModeState,
                       int minDrawingPlayers, int maxDrawingPlayers,
                       int minGuessingPlayers, int maxGuessingPlayers)
    {
        this.maxPlayers = maxPlayers;
        this.gameRounds = gameRounds;
        this.gameModeState = gameModeState;
        this.minDrawingPlayers = minDrawingPlayers;
        this.maxDrawingPlayers = maxDrawingPlayers;
        this.minGuessingPlayers = minGuessingPlayers;
        this.maxGuessingPlayers = maxGuessingPlayers;
    }
}
