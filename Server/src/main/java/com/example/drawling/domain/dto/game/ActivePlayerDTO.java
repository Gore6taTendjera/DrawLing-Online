package com.example.drawling.domain.dto.game;

import com.example.drawling.domain.enums.game.GamePlayerRole;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ActivePlayerDTO {
    private String playerName;
    private double balance;
    private GamePlayerRole role;
    private String profilePicture;


    public ActivePlayerDTO(String playerName, double balance, GamePlayerRole role) {
        this.playerName = playerName;
        this.balance = balance;
        this.role = role;
    }

    public ActivePlayerDTO(String playerName, double balance, GamePlayerRole role, String profilePicture) {
        this.playerName = playerName;
        this.balance = balance;
        this.role = role;
        this.profilePicture = profilePicture;
    }
}
