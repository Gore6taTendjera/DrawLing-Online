package com.example.drawling.domain.dto.game;


import com.example.drawling.domain.enums.game.GameModeEnum;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CreateGameLobbyRequestDTO {
    private int maxPlayers;
    private GameModeEnum gameModeName;
    private List<GameRoundRequestDTO> gameRounds;

}
