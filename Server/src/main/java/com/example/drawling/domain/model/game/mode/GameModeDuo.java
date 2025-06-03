package com.example.drawling.domain.model.game.mode;

import com.example.drawling.domain.enums.game.GameModeState;
import com.example.drawling.domain.model.game.PlayerSession;
import com.example.drawling.domain.model.game.round.GameRound;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class GameModeDuo extends GameMode {
    private List<PlayerSession> drawingPlayer;
    private List<PlayerSession> guessingPlayers;

    public GameModeDuo(int maxPlayers, List<GameRound> gameRounds) {
        super(maxPlayers, gameRounds, GameModeState.LOBBY);
        this.drawingPlayer = new ArrayList<>();
        this.guessingPlayers = new ArrayList<>();
    }

}
