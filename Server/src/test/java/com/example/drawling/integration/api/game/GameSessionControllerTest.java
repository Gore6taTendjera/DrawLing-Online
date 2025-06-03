package com.example.drawling.integration.api.game;

import com.example.drawling.application.controller.game.GameSessionController;
import com.example.drawling.business.creator.game.GameModeCreator;
import com.example.drawling.business.interfaces.service.game.GameSessionService;
import com.example.drawling.domain.dto.game.CreateGameLobbyRequestDTO;
import com.example.drawling.domain.dto.game.GameRoundRequestDTO;
import com.example.drawling.domain.enums.game.GameModeEnum;
import com.example.drawling.domain.enums.game.GameRoundEnum;
import com.example.drawling.domain.enums.game.GameWordCategoryEnum;
import com.example.drawling.domain.model.game.GameSession;
import com.example.drawling.domain.model.game.mode.GameMode;
import com.example.drawling.statics.GameStaticSessions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GameSessionControllerTest {

    @Mock
    private GameSessionService gameSessionService;

    @Mock
    private GameModeCreator gameModeCreator;

    @InjectMocks
    private GameSessionController gameSessionController;

    @Test
    void testGetSession_ExistingSession() {
        String link = "testLink";
        try (MockedStatic<GameStaticSessions> mockedStatic = mockStatic(GameStaticSessions.class)) {
            mockedStatic.when(() -> GameStaticSessions.sessionExists(link)).thenReturn(true);

            ResponseEntity<String> response = gameSessionController.getSession(link);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(link, response.getBody());
        }
    }

    @Test
    void testGetSession_NonExistingSession() {
        String link = "nonExistentLink";
        try (MockedStatic<GameStaticSessions> mockedStatic = mockStatic(GameStaticSessions.class)) {
            mockedStatic.when(() -> GameStaticSessions.sessionExists(link)).thenReturn(false);

            ResponseEntity<String> response = gameSessionController.getSession(link);

            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        }
    }

    @Test
    void testCreateGameLobby_ValidRequest() {
        CreateGameLobbyRequestDTO requestDTO = new CreateGameLobbyRequestDTO();
        requestDTO.setMaxPlayers(4);
        requestDTO.setGameModeName(GameModeEnum.NORMAL);

        List<GameRoundRequestDTO> gameRounds = new ArrayList<>();
        gameRounds.add(createGameRoundRequest(GameRoundEnum.NORMAL, 60, GameWordCategoryEnum.GEOGRAPHY));
        gameRounds.add(createGameRoundRequest(GameRoundEnum.FAST, 90, GameWordCategoryEnum.FOOD));
        requestDTO.setGameRounds(gameRounds);

        GameMode mockGameMode = mock(GameMode.class);
        when(gameModeCreator.createGameMode(anyInt(), anyList(), any(GameModeEnum.class))).thenReturn(mockGameMode);
        when(gameSessionService.addGameSession(any(GameSession.class))).thenReturn("testLink");

        ResponseEntity<String> response = gameSessionController.createGameLobby(requestDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("testLink", response.getBody());
    }

    @Test
    void testCreateGameLobby_InvalidRequest_EmptyRounds() {
        CreateGameLobbyRequestDTO requestDTO = new CreateGameLobbyRequestDTO();
        requestDTO.setMaxPlayers(4);
        requestDTO.setGameModeName(GameModeEnum.NORMAL);
        requestDTO.setGameRounds(new ArrayList<>()); // Empty rounds

        ResponseEntity<String> response = gameSessionController.createGameLobby(requestDTO);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Game rounds cannot be empty.", response.getBody());
    }



    private GameRoundRequestDTO createGameRoundRequest(GameRoundEnum roundEnum, int duration, GameWordCategoryEnum wordCategory) {
        GameRoundRequestDTO requestDTO = new GameRoundRequestDTO();
        requestDTO.setGameRoundEnum(roundEnum);
        requestDTO.setDuration(duration);
        requestDTO.setWordCategory(wordCategory);
        return requestDTO;
    }
}
