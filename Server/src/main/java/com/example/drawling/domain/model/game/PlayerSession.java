package com.example.drawling.domain.model.game;

import com.example.drawling.domain.enums.game.GamePlayerRole;
import com.example.drawling.domain.model.Player;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlayerSession {
    private String sessionId;
    private GamePlayerRole role;
    private Player player;

    public PlayerSession(String sessionId, Player player) {
        this.sessionId = sessionId;
        this.player = player;
    }

}
