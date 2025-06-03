package com.example.drawling.business.game.mode;

import com.example.drawling.business.creator.game.GameModeCreator;
import com.example.drawling.business.creator.game.GameRoundCreator;
import com.example.drawling.domain.dto.game.GameRoundRequestDTO;
import com.example.drawling.domain.enums.game.GameModeEnum;
import com.example.drawling.domain.enums.game.GameRoundEnum;
import com.example.drawling.domain.model.game.mode.*;
import com.example.drawling.domain.model.game.round.GameRound;
import com.example.drawling.domain.model.game.round.GameRoundNormal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GameModeCreatorTest {

    @Mock
    private GameRoundCreator gameRoundCreator;

    @InjectMocks
    private GameModeCreator gameModeCreator;

    @BeforeEach
    void setUp() {
        // mocks
    }


    @Test
    void testCreateGameModeThrowsExceptionWhenMaxPlayersIsZeroOrNegative() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                this::createGameModeWithInvalidPlayers);

        assertEquals("Max players must be greater than zero.", exception.getMessage());
    }

    private void createGameModeWithInvalidPlayers() {
        gameModeCreator.createGameMode(0, List.of(new GameRoundRequestDTO()), GameModeEnum.NORMAL);
    }


    @Test
    void testCreateGameModeThrowsExceptionWhenGameRoundsRequestIsNull() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> gameModeCreator.createGameMode(5, null, GameModeEnum.NORMAL));
        assertEquals("Game rounds request cannot be null or empty.", exception.getMessage());
    }

    @Test
    void testCreateGameModeThrowsExceptionWhenGameRoundsRequestIsEmpty() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                this::createGameModeWithEmptyGameRounds);

        assertEquals("Game rounds request cannot be null or empty.", exception.getMessage());
    }

    private void createGameModeWithEmptyGameRounds() {
        gameModeCreator.createGameMode(5, List.of(), GameModeEnum.NORMAL);
    }


    @Test
    void testCreateGameModeThrowsExceptionWhenGameModeNameIsNull() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> createGameModeWithNullGameModeName(5));

        assertEquals("Game mode name cannot be null.", exception.getMessage());
    }

    private void createGameModeWithNullGameModeName(int maxPlayers) {
        gameModeCreator.createGameMode(maxPlayers, List.of(new GameRoundRequestDTO()), null);
    }

    @Test
    void testCreateGameModeThrowsExceptionForInvalidGameModeName() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                this::createGameModeWithInvalidGameMode);

        assertFalse(exception.getMessage().startsWith("Invalid game mode:"));
    }

    private void createGameModeWithInvalidGameMode() {
        gameModeCreator.createGameMode(5, List.of(new GameRoundRequestDTO()), GameModeEnum.valueOf("INVALID"));
    }


    @Test
    void testCreateGameModeCreatesNormalGameMode() {
        List<GameRoundRequestDTO> gameRoundRequests = List.of(new GameRoundRequestDTO());
        List<GameRoundNormal> gameRounds = List.of(new GameRoundNormal(60, "example"));
        when(gameRoundCreator.createGameRounds(gameRoundRequests)).thenReturn(List.copyOf(gameRounds));

        GameMode gameMode = gameModeCreator.createGameMode(5, gameRoundRequests, GameModeEnum.NORMAL);

        assertInstanceOf(GameModeNormal.class, gameMode);
        assertEquals(5, gameMode.getMaxPlayers());
        assertEquals(gameRounds, gameMode.getGameRounds());
    }

    @Test
    void testCreateGameModeCreatesDuoGameMode() {
        List<GameRoundRequestDTO> gameRoundRequests = List.of(new GameRoundRequestDTO());
        List<GameRoundNormal> gameRounds = List.of(new GameRoundNormal(60, "example"));
        when(gameRoundCreator.createGameRounds(gameRoundRequests)).thenReturn(List.copyOf(gameRounds));

        GameMode gameMode = gameModeCreator.createGameMode(2, gameRoundRequests, GameModeEnum.DUO);

        assertInstanceOf(GameModeDuo.class, gameMode);
        assertEquals(2, gameMode.getMaxPlayers());
        assertEquals(gameRounds, gameMode.getGameRounds());
    }

    @Test
    void testCreateGameModeCreatesTrioGameMode() {
        List<GameRoundRequestDTO> gameRoundRequests = List.of(new GameRoundRequestDTO());
        List<GameRoundNormal> gameRounds = List.of(new GameRoundNormal(60, "example"));
        when(gameRoundCreator.createGameRounds(gameRoundRequests)).thenReturn(List.copyOf(gameRounds));

        GameMode gameMode = gameModeCreator.createGameMode(3, gameRoundRequests, GameModeEnum.TRIO);

        assertInstanceOf(GameModeTrio.class, gameMode);
        assertEquals(3, gameMode.getMaxPlayers());
        assertEquals(gameRounds, gameMode.getGameRounds());
    }

    @Test
    void testCreateGameModeCreatesCombinedGameMode() {
        List<GameRoundRequestDTO> gameRoundRequests = List.of(new GameRoundRequestDTO());
        List<GameRoundNormal> gameRounds = List.of(new GameRoundNormal(60, "example"));
        when(gameRoundCreator.createGameRounds(gameRoundRequests)).thenReturn(List.copyOf(gameRounds));

        GameMode gameMode = gameModeCreator.createGameMode(10, gameRoundRequests, GameModeEnum.COMBINED);

        assertInstanceOf(GameModeCombined.class, gameMode);
        assertEquals(10, gameMode.getMaxPlayers());
        assertEquals(gameRounds, gameMode.getGameRounds());
    }


    @Test
    void testCreateGameModeNormal() {
        // Arrange
        int maxPlayers = 4;
        List<GameRoundRequestDTO> gameRoundsRequest = new ArrayList<>();

        GameRoundRequestDTO requestDTO = new GameRoundRequestDTO();
        requestDTO.setGameRoundEnum(GameRoundEnum.NORMAL);
        gameRoundsRequest.add(requestDTO);

        List<GameRound> gameRounds = new ArrayList<>();
        when(gameRoundCreator.createGameRounds(gameRoundsRequest)).thenReturn(gameRounds);

        // Act
        GameMode gameMode = gameModeCreator.createGameMode(maxPlayers, gameRoundsRequest, GameModeEnum.NORMAL);

        // Assert
        assertNotNull(gameMode);
        assertInstanceOf(GameModeNormal.class, gameMode);
        verify(gameRoundCreator).createGameRounds(gameRoundsRequest);
    }

    @Test
    void testCreateGameModeFast() {
        // Arrange
        int maxPlayers = 4;
        List<GameRoundRequestDTO> gameRoundsRequest = new ArrayList<>();

        GameRoundRequestDTO requestDTO = new GameRoundRequestDTO();
        requestDTO.setGameRoundEnum(GameRoundEnum.FAST);
        gameRoundsRequest.add(requestDTO);

        List<GameRound> gameRounds = new ArrayList<>();
        when(gameRoundCreator.createGameRounds(gameRoundsRequest)).thenReturn(gameRounds);

        // Act
        GameMode gameMode = gameModeCreator.createGameMode(maxPlayers, gameRoundsRequest, GameModeEnum.NORMAL);

        // Assert
        assertNotNull(gameMode);
        assertInstanceOf(GameModeNormal.class, gameMode);
        verify(gameRoundCreator).createGameRounds(gameRoundsRequest);
    }

    @Test
    void testCreateGameModeInvalid() {
        // Arrange
        int maxPlayers = 4;
        List<GameRoundRequestDTO> gameRoundsRequest = new ArrayList<>();
        GameRoundRequestDTO requestDTO = new GameRoundRequestDTO();
        requestDTO.setGameRoundEnum(GameRoundEnum.NORMAL);
        gameRoundsRequest.add(requestDTO);

        // Act & Assert
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            gameModeCreator.createGameMode(maxPlayers, gameRoundsRequest, null);
        });

        assertEquals("Game mode name cannot be null.", thrown.getMessage());
    }


}