package com.example.drawling.application.controller.game;

import com.example.drawling.business.creator.game.GameModeCreator;
import com.example.drawling.business.interfaces.service.game.GameSessionService;
import com.example.drawling.domain.dto.game.CreateGameLobbyRequestDTO;
import com.example.drawling.domain.dto.game.GameRoundRequestDTO;
import com.example.drawling.domain.enums.game.GameModeEnum;
import com.example.drawling.domain.enums.game.GameSessionState;
import com.example.drawling.domain.model.game.GameSession;
import com.example.drawling.domain.model.game.mode.GameMode;
import com.example.drawling.statics.GameStaticSessions;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/game-sessions")
public class GameSessionController {
    private final GameSessionService gameSessionService;
    private final GameModeCreator gameModeCreator;

    public GameSessionController(GameSessionService gameSessionService, GameModeCreator gameModeCreator) {
        this.gameSessionService = gameSessionService;
        this.gameModeCreator = gameModeCreator;
    }


    @GetMapping("/get/{link}")
    public ResponseEntity<String> getSession(@PathVariable String link) {
        boolean exist = GameStaticSessions.sessionExists(link);
        if (!exist) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(link);
    }

    @PreAuthorize("hasAuthority('USER_CREATE_LOBBY')")
    @PostMapping("/create-real")
    public ResponseEntity<String> createGameLobby(@RequestBody CreateGameLobbyRequestDTO createGameLobbyRequestDTO) {
        int maxPlayers = createGameLobbyRequestDTO.getMaxPlayers();
        GameModeEnum gameModeName = createGameLobbyRequestDTO.getGameModeName();

        List<GameRoundRequestDTO> gameRounds = createGameLobbyRequestDTO.getGameRounds();

        if (gameRounds == null || gameRounds.isEmpty()) {
            return ResponseEntity.badRequest().body("Game rounds cannot be empty.");
        }

        GameMode gameMode = gameModeCreator.createGameMode(maxPlayers, gameRounds, gameModeName);

        String link = gameSessionService.addGameSession(new GameSession(gameMode, GameSessionState.STANDBY));

        return ResponseEntity.ok(link);
    }

}
