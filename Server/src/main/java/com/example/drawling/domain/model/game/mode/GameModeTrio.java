package com.example.drawling.domain.model.game.mode;

import com.example.drawling.domain.enums.game.GameModeState;
import com.example.drawling.domain.model.game.round.GameRound;

import java.util.List;

/**
 * DEBUG OUTPUT:
 * Player added to game mode: com.example.drawling.domain.model.game.mode.GameModeTrio@41c9a873
 * Player added to game mode: com.example.drawling.domain.model.game.mode.GameModeTrio@41c9a873
 * Game Session: com.example.drawling.domain.model.game.GameSession@45f63492
 * Game mode: com.example.drawling.domain.model.game.mode.GameModeTrio@41c9a873
 * Game mode rounds: []
 *
 * EXPLANATION:
 * currently this game mode is not implemented
 * which means that the system is going to search for the game rounds
 * which are not available, but you can take a look at the 'NORMAL' AND 'DUO' game modes.
 *
 * 'DUO' DEBUG OUTPUT (currently the same goes for 'NORMAL' too):
 * Player added to game mode: com.example.drawling.domain.model.game.mode.GameModeDuo@7e30fff1
 * Player added to game mode: com.example.drawling.domain.model.game.mode.GameModeDuo@7e30fff1
 * Game Session: com.example.drawling.domain.model.game.GameSession@7a0f74ef
 * Game mode: com.example.drawling.domain.model.game.mode.GameModeDuo@7e30fff1
 * Game mode rounds: [com.example.drawling.domain.model.game.round.GameRoundNormal@3f0df5ca, com.example.drawling.domain.model.game.round.GameRoundFast@774e1e53]
 */
public class GameModeTrio extends GameMode {


    public GameModeTrio(int maxPlayers, List<GameRound> gameRounds) {
        super(maxPlayers, gameRounds, GameModeState.LOBBY);
    }


}
