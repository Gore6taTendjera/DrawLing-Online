package com.example.drawling.business.game.session;

import com.example.drawling.business.helper.UniqueIdGeneratorHelper;
import com.example.drawling.business.implementation.game.session.GameSessionServiceImpl;
import com.example.drawling.domain.enums.game.GamePlayerRole;
import com.example.drawling.domain.model.game.GameSession;
import com.example.drawling.domain.model.game.PlayerSession;
import com.example.drawling.domain.model.game.mode.GameMode;
import com.example.drawling.domain.model.game.round.GameRound;
import com.example.drawling.exception.GameSessionNotFoundException;
import com.example.drawling.exception.InsufficientPlayersException;
import com.example.drawling.exception.InvalidGameSessionException;
import com.example.drawling.exception.PlayerSessionNotFoundException;
import com.example.drawling.statics.GameStaticSessions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GameSessionServiceImplTest {

    @Mock
    private UniqueIdGeneratorHelper uniqueIdGenerator;

    private MockedStatic<GameStaticSessions> mockedStatic;

    @InjectMocks
    private GameSessionServiceImpl gameSessionService;

    @Mock
    private GameSession gameSession;

    @Mock
    private PlayerSession playerSession;

    @Mock
    private GameMode gameMode;

    @Mock
    private GameRound gameRound;


    @BeforeEach
    void setUp() {
        mockedStatic = Mockito.mockStatic(GameStaticSessions.class);
    }


    @AfterEach
    void tearDown() {
        mockedStatic.close();
    }






    @Test
    void testAddPlayerWhenDrawingCountLessThanMaxButGreaterThanMin() {
        // Arrange
        int maxDrawingPlayers = 3;
        int minDrawingPlayers = 1;
        int minGuessingPlayers = 1;
        int maxGuessingPlayers = 3;

        Mockito.when(gameSession.getGameMode()).thenReturn(gameMode);
        when(gameMode.getMaxPlayers()).thenReturn(50);
        Mockito.when(gameMode.getMinDrawingPlayers()).thenReturn(minDrawingPlayers);
        Mockito.when(gameMode.getMaxDrawingPlayers()).thenReturn(maxDrawingPlayers);
        Mockito.when(gameMode.getMinGuessingPlayers()).thenReturn(minGuessingPlayers);
        Mockito.when(gameMode.getMaxGuessingPlayers()).thenReturn(maxGuessingPlayers);

        Map<String, PlayerSession> players = new HashMap<>();
        players.put("player1", createPlayerSession(GamePlayerRole.DRAWING));
        players.put("player2", createPlayerSession(GamePlayerRole.GUESSING));
        Mockito.when(gameSession.getPlayers()).thenReturn(players);

        // Act
        gameSessionService.addPlayer(gameSession, playerSession);

        // Assert
        Mockito.verify(playerSession).setRole(GamePlayerRole.DRAWING);
        Mockito.verify(gameSession, Mockito.atLeastOnce()).getPlayers();
    }

    @Test
    void testAddPlayerWhenGuessingCountLessThanMaxButGreaterThanMin() {
        // Arrange
        int maxDrawingPlayers = 2; // Adjust to create a full drawing count
        int minDrawingPlayers = 1;
        int minGuessingPlayers = 1;
        int maxGuessingPlayers = 3;

        Mockito.when(gameSession.getGameMode()).thenReturn(gameMode);
        when(gameMode.getMaxPlayers()).thenReturn(50);
        Mockito.when(gameMode.getMinDrawingPlayers()).thenReturn(minDrawingPlayers);
        Mockito.when(gameMode.getMaxDrawingPlayers()).thenReturn(maxDrawingPlayers);
        Mockito.when(gameMode.getMinGuessingPlayers()).thenReturn(minGuessingPlayers);
        Mockito.when(gameMode.getMaxGuessingPlayers()).thenReturn(maxGuessingPlayers);

        // Fill drawing roles to the max limit
        Map<String, PlayerSession> players = new HashMap<>();
        players.put("player1", createPlayerSession(GamePlayerRole.DRAWING));
        players.put("player2", createPlayerSession(GamePlayerRole.DRAWING));

        // Add a player in guessing role below the max limit
        players.put("player3", createPlayerSession(GamePlayerRole.GUESSING));

        Mockito.when(gameSession.getPlayers()).thenReturn(players);

        // Act
        gameSessionService.addPlayer(gameSession, playerSession);

        // Assert
        Mockito.verify(playerSession).setRole(GamePlayerRole.GUESSING);
        Mockito.verify(gameSession, Mockito.atLeastOnce()).getPlayers();
    }

    private PlayerSession createPlayerSession(GamePlayerRole role) {
        PlayerSession playerSession2 = Mockito.mock(PlayerSession.class);
        Mockito.when(playerSession2.getRole()).thenReturn(role);
        return playerSession2;
    }


    @Test
    void testAddPlayer_RoleAssignedAsGuessing() {
        // Arrange
        // Mock Map
        Map<String, PlayerSession> mockPlayers = mock(Map.class);
        when(gameSession.getPlayers()).thenReturn(mockPlayers);

        // Configure GameMode
        when(gameSession.getGameMode()).thenReturn(gameMode);
        when(gameMode.getMaxPlayers()).thenReturn(50);
        when(gameMode.getMinDrawingPlayers()).thenReturn(1);
        when(gameMode.getMaxDrawingPlayers()).thenReturn(3);
        when(gameMode.getMinGuessingPlayers()).thenReturn(1);
        when(gameMode.getMaxGuessingPlayers()).thenReturn(3);

        // Simulate existing drawing player
        PlayerSession drawingPlayer = createMockPlayer(GamePlayerRole.DRAWING);
        when(mockPlayers.values()).thenReturn(List.of(drawingPlayer));

        // Mock playerSession behavior
        when(playerSession.getSessionId()).thenReturn("sessionId");

        // Act
        boolean result = gameSessionService.addPlayer(gameSession, playerSession);

        // Assert
        assertTrue(result);
        verify(playerSession).setRole(GamePlayerRole.GUESSING); // Verify role assignment
        verify(mockPlayers).put(playerSession.getSessionId(), playerSession); // Verify addition to Map
    }


    private PlayerSession createMockPlayer(GamePlayerRole role) {
        PlayerSession mockPlayer = mock(PlayerSession.class);
        // Ensure stubbing is completed
        when(mockPlayer.getRole()).thenReturn(role);
        return mockPlayer;
    }


    @Test
    void testAddPlayer_ValidRoleAssignment() {
        // Arrange
        Map<String, PlayerSession> players = mock(Map.class); // Mock the Map
        when(gameSession.getPlayers()).thenReturn(players); // Return the mocked Map

        when(gameSession.getGameMode()).thenReturn(gameMode);
        when(gameMode.getMaxPlayers()).thenReturn(50);
        when(gameMode.getMinDrawingPlayers()).thenReturn(1);
        when(gameMode.getMaxDrawingPlayers()).thenReturn(3);
        when(gameMode.getMinGuessingPlayers()).thenReturn(1);
        when(gameMode.getMaxGuessingPlayers()).thenReturn(3);

        when(playerSession.getSessionId()).thenReturn("sessionId");

        // Act
        boolean result = gameSessionService.addPlayer(gameSession, playerSession);

        // Assert
        assertTrue(result);
        verify(playerSession).setRole(GamePlayerRole.DRAWING); // Verifying role assignment
        verify(players).put(playerSession.getSessionId(), playerSession); // Verify the mocked Map's put method was called
    }


    @Test
    void testAddPlayer_RoleAssignedAsDrawing() {
        // Arrange
        // Mock the Map returned by getPlayers() method
        Map<String, PlayerSession> players = mock(Map.class); // Mock the Map
        when(gameSession.getPlayers()).thenReturn(players); // Ensure getPlayers() returns the mocked Map

        // Mock the game mode
        when(gameSession.getGameMode()).thenReturn(gameMode);
        when(gameMode.getMaxPlayers()).thenReturn(60);
        when(gameMode.getMinDrawingPlayers()).thenReturn(1);
        when(gameMode.getMaxDrawingPlayers()).thenReturn(3);
        when(gameMode.getMinGuessingPlayers()).thenReturn(1);
        when(gameMode.getMaxGuessingPlayers()).thenReturn(3);

        // Mock the player's session and role
        when(playerSession.getSessionId()).thenReturn("sessionId");

        // Act
        boolean result = gameSessionService.addPlayer(gameSession, playerSession);

        // Assert
        assertTrue(result); // The player should be successfully added
        verify(playerSession).setRole(GamePlayerRole.DRAWING); // Verify that the role was set correctly
        verify(players).put(playerSession.getSessionId(), playerSession); // Verify the player was added to the mocked players map
    }


    @Test
    void testGetGameSessionByPlayerSessionId_PlayerNotFound() {
        // Arrange
        String playerSessionId = "non-existent-session-id";
        Map<String, GameSession> sessions = new HashMap<>();
        mockedStatic.when(GameStaticSessions::getReadOnlySessions).thenReturn(sessions); // Use existing static mock

        // Act & Assert
        assertThrows(PlayerSessionNotFoundException.class,
                () -> gameSessionService.getGameSessionByPlayerSessionId(playerSessionId));
    }


    @Test
    void testAddPlayer_MaxPlayersReached() {
        // Arrange
        GameMode gameMode2 = mock(GameMode.class);
        when(gameMode2.getMaxPlayers()).thenReturn(1);
        when(gameSession.getGameMode()).thenReturn(gameMode2);

        Map<String, PlayerSession> players = new HashMap<>();
        players.put("existing-player", mock(PlayerSession.class));
        when(gameSession.getPlayers()).thenReturn(players);

        PlayerSession newPlayerSession = mock(PlayerSession.class);

        // Act & Assert
        assertThrows(InsufficientPlayersException.class, () -> gameSessionService.addPlayer(gameSession, newPlayerSession));
    }

    @Test
    void testGetAllPlayers_NullGameSession() {
        // Act
        InvalidGameSessionException thrown = assertThrows(InvalidGameSessionException.class,
                () -> gameSessionService.getAllPlayers(null));

        // Assert
        assertEquals("Game session or players list is null", thrown.getMessage());
    }

    @Test
    void testCyclePlayers_InsufficientPlayers() {
        // Arrange
        Map<String, PlayerSession> players = new HashMap<>();
        when(gameSession.getPlayers()).thenReturn(players);

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> gameSessionService.cyclePlayers(gameSession));
    }

    @Test
    void testKickPlayer_InvalidSessionId() {
        // Arrange
        Map<String, PlayerSession> players = new HashMap<>();
        when(gameSession.getPlayers()).thenReturn(players);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> gameSessionService.kickPlayer(gameSession, "invalid-session-id"));
    }


    @Test
    void testGetPlayerBySessionAndPlayerSessionId() {
        // Arrange
        String sessionId = "player-session-id";

        PlayerSession playerSession2 = new PlayerSession();
        playerSession2.setSessionId(sessionId);

        Map<String, PlayerSession> players = new HashMap<>();
        players.put(sessionId, playerSession2);

        when(gameSession.getPlayers()).thenReturn(players);

        // Act
        PlayerSession result = gameSessionService.getPlayerBySessionAndPlayerSessionId(gameSession, sessionId);

        // Assert
        assertNotNull(result);
        assertEquals(sessionId, result.getSessionId());
    }


    @Test
    void testGetPlayerBySessionId_PlayerNotFound() {
        // Arrange
        String sessionId = "non-existent-session-id";
        Map<String, GameSession> sessions = new HashMap<>();
        mockedStatic.when(GameStaticSessions::getReadOnlySessions).thenReturn(sessions); // Use existing static mock

        // Act & Assert
        assertThrows(PlayerSessionNotFoundException.class,
                () -> gameSessionService.getPlayerBySessionId(sessionId));
    }


    @Test
    void testGetAllPlayers() {
        // Arrange
        Map<String, PlayerSession> players = new HashMap<>();
        players.put("player-id-1", playerSession);
        when(gameSession.getPlayers()).thenReturn(players);

        // Act
        List<PlayerSession> result = gameSessionService.getAllPlayers(gameSession);

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void testGetActiveGameRound() {
        // Arrange
        GameMode gameMode2 = mock(GameMode.class);
        when(gameMode2.getGameRounds()).thenReturn(List.of(gameRound));

        when(gameSession.getGameMode()).thenReturn(gameMode2);

        when(gameRound.isActive()).thenReturn(true);

        // Act
        GameRound result = gameSessionService.getActiveGameRound(gameSession);

        // Assert
        assertNotNull(result);
    }


    @Test
    void testCyclePlayers() {
        // Arrange
        PlayerSession drawingPlayer = mock(PlayerSession.class);
        PlayerSession guessingPlayer = mock(PlayerSession.class);

        when(drawingPlayer.getRole()).thenReturn(GamePlayerRole.DRAWING);
        when(guessingPlayer.getRole()).thenReturn(GamePlayerRole.GUESSING);

        Map<String, PlayerSession> players = new HashMap<>();
        players.put("drawing-id", drawingPlayer);
        players.put("guessing-id", guessingPlayer);
        when(gameSession.getPlayers()).thenReturn(players);

        // Act
        gameSessionService.cyclePlayers(gameSession);

        // Assert
        verify(drawingPlayer).setRole(GamePlayerRole.GUESSING);
        verify(guessingPlayer).setRole(GamePlayerRole.DRAWING);
    }


    @Test
    void testGetGameSessionById_Success() {
        // Arrange
        String sessionId = "validSessionId";
        GameSession expectedSession = gameSession;

        mockedStatic.when(() -> GameStaticSessions.getByLink(sessionId)).thenReturn(expectedSession);

        // Act
        GameSession result = gameSessionService.getGameSessionById(sessionId);

        // Assert
        assertEquals(expectedSession, result);
        mockedStatic.verify(() -> GameStaticSessions.getByLink(sessionId), times(1));
    }

    @Test
    void testGetGameSessionById_SessionNotFound() {
        // Arrange
        String sessionId = "invalidSessionId";

        mockedStatic.when(() -> GameStaticSessions.getByLink(sessionId)).thenThrow(new NoSuchElementException("No session found"));

        // Act & Assert
        GameSessionNotFoundException exception = assertThrows(
                GameSessionNotFoundException.class,
                () -> gameSessionService.getGameSessionById(sessionId)
        );

        assertEquals("Game session not found for session ID: " + sessionId, exception.getMessage());
        mockedStatic.verify(() -> GameStaticSessions.getByLink(sessionId), times(1));
    }


    @Test
    void testGetPlayerBySessionId_Success() {
        // Arrange
        String sessionId = "validSessionId";
        Map<String, GameSession> sessions = new HashMap<>();
        Map<String, PlayerSession> players = new HashMap<>();

        players.put(sessionId, playerSession);
        when(gameSession.getPlayers()).thenReturn(players);
        sessions.put("gameSession1", gameSession);

        mockedStatic.when(GameStaticSessions::getReadOnlySessions).thenReturn(sessions);

        // Act
        PlayerSession result = gameSessionService.getPlayerBySessionId(sessionId);

        // Assert
        assertEquals(playerSession, result);
        mockedStatic.verify(GameStaticSessions::getReadOnlySessions, times(1));
    }

    @Test
    void testGetPlayerBySessionId_SessionIdIsNull() {
        // Arrange
        String sessionId = null;

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> gameSessionService.getPlayerBySessionId(sessionId)
        );

        assertEquals("Session ID cannot be null or empty", exception.getMessage());
        mockedStatic.verify(GameStaticSessions::getReadOnlySessions, never());
    }

    @Test
    void testGetPlayerBySessionId_SessionIdIsEmpty() {
        // Arrange
        String sessionId = "";

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> gameSessionService.getPlayerBySessionId(sessionId)
        );

        assertEquals("Session ID cannot be null or empty", exception.getMessage());
        mockedStatic.verify(GameStaticSessions::getReadOnlySessions, never());
    }


    @Test
    void testGetPlayerBySessionId_NoSessionsAvailable() {
        // Arrange
        mockedStatic.when(GameStaticSessions::getReadOnlySessions).thenReturn(Collections.emptyMap());

        // Act & Assert
        PlayerSessionNotFoundException exception = assertThrows(
                PlayerSessionNotFoundException.class,
                () -> gameSessionService.getPlayerBySessionId("someSessionId")
        );

        assertEquals("Player session not found for session ID: someSessionId", exception.getMessage());
        mockedStatic.verify(GameStaticSessions::getReadOnlySessions, times(1));
    }








    @Test
    void testKickPlayerWhenDrawingPlayerIsReplaced() {
        // Arrange
        Map<String, PlayerSession> players = new HashMap<>();
        PlayerSession drawingPlayer = createPlayerSession(GamePlayerRole.DRAWING);
        PlayerSession guessingPlayer = createPlayerSession(GamePlayerRole.GUESSING);
        players.put("drawingPlayer", drawingPlayer);
        players.put("guessingPlayer", guessingPlayer);

        Mockito.when(gameSession.getPlayers()).thenReturn(players);

        // Act
        boolean result = gameSessionService.kickPlayer(gameSession, "drawingPlayer");

        // Assert
        Assertions.assertTrue(result);
        Mockito.verify(guessingPlayer).setRole(GamePlayerRole.DRAWING);
        Assertions.assertFalse(players.containsKey("drawingPlayer"));
    }

    @Test
    void testKickPlayerWhenNoGuessingPlayerToReplaceDrawingPlayer() {
        // Arrange
        Map<String, PlayerSession> players = new HashMap<>();
        PlayerSession drawingPlayer = createPlayerSession(GamePlayerRole.DRAWING);
        players.put("drawingPlayer", drawingPlayer);

        Mockito.when(gameSession.getPlayers()).thenReturn(players);

        // Act & Assert
        IllegalStateException exception = Assertions.assertThrows(
                IllegalStateException.class,
                () -> gameSessionService.kickPlayer(gameSession, "drawingPlayer")
        );

        Assertions.assertEquals("No guessing player found to replace the drawing player", exception.getMessage());
    }

    @Test
    void testKickPlayerWhenPlayerIsNotFound() {
        // Arrange
        Map<String, PlayerSession> players = new HashMap<>();
        Mockito.when(gameSession.getPlayers()).thenReturn(players);

        // Act & Assert
        IllegalArgumentException exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> gameSessionService.kickPlayer(gameSession, "nonexistentPlayer")
        );

        Assertions.assertEquals("Player session not found: nonexistentPlayer", exception.getMessage());
    }

    @Test
    void testKickPlayerWhenNotDrawingPlayer() {
        // Arrange
        Map<String, PlayerSession> players = new HashMap<>();
        PlayerSession guessingPlayer = createPlayerSession(GamePlayerRole.GUESSING);
        players.put("guessingPlayer", guessingPlayer);

        Mockito.when(gameSession.getPlayers()).thenReturn(players);

        // Act
        boolean result = gameSessionService.kickPlayer(gameSession, "guessingPlayer");

        // Assert
        Assertions.assertTrue(result);
        Assertions.assertFalse(players.containsKey("guessingPlayer"));
    }


    @Test
    void testAddGameSession() {
        // Arrange
        String generatedUniqueId = "unique-session-id";
        Mockito.when(uniqueIdGenerator.generateUniqueId()).thenReturn(generatedUniqueId);

        // Act
        String result = gameSessionService.addGameSession(gameSession);

        // Assert
        Assertions.assertEquals(generatedUniqueId, result, "The returned unique ID should match the generated ID.");
        Mockito.verify(gameSession).setSessionId(generatedUniqueId);
        GameStaticSessions.addSession(generatedUniqueId, gameSession);
    }

    @Test
    void testGetPlayerBySessionAndPlayerSessionIdWhenGameSessionIsNull() {
        // Arrange
        String sessionId = "test-session-id";

        // Act & Assert
        IllegalArgumentException exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> gameSessionService.getPlayerBySessionAndPlayerSessionId(null, sessionId),
                "Expected an IllegalArgumentException for null gameSession"
        );
        Assertions.assertEquals("Game session cannot be null", exception.getMessage());
    }


    @Test
    void testGetPlayerBySessionAndPlayerSessionIdWhenSessionIdIsNull() {
        // Act & Assert
        IllegalArgumentException exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> gameSessionService.getPlayerBySessionAndPlayerSessionId(gameSession, null),
                "Expected an IllegalArgumentException for null sessionId"
        );
        Assertions.assertEquals("Session ID cannot be null or empty", exception.getMessage());
    }


    @Test
    void testGetPlayerBySessionAndPlayerSessionIdWhenSessionIdIsEmpty() {

        // Act & Assert
        IllegalArgumentException exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> gameSessionService.getPlayerBySessionAndPlayerSessionId(gameSession, ""),
                "Expected an IllegalArgumentException for empty sessionId"
        );
        Assertions.assertEquals("Session ID cannot be null or empty", exception.getMessage());
    }


    @Test
    void testGetPlayerBySessionAndPlayerSessionIdWhenPlayerSessionNotFound() {
        // Arrange
        String sessionId = "test-session-id";
        Mockito.when(gameSession.getPlayers()).thenReturn(new HashMap<>());

        // Act
        PlayerSession result = gameSessionService.getPlayerBySessionAndPlayerSessionId(gameSession, sessionId);

        // Assert
        Assertions.assertNull(result, "Expected null when player session is not found");
    }


    @Test
    void testGetPlayerBySessionAndPlayerSessionIdWhenPlayerSessionIsFound() {
        // Arrange
        String sessionId = "test-session-id";
        PlayerSession expectedPlayerSession = new PlayerSession();
        Map<String, PlayerSession> players = new HashMap<>();
        players.put(sessionId, expectedPlayerSession);

        Mockito.when(gameSession.getPlayers()).thenReturn(players);

        // Act
        PlayerSession result = gameSessionService.getPlayerBySessionAndPlayerSessionId(gameSession, sessionId);

        // Assert
        Assertions.assertEquals(expectedPlayerSession, result, "Expected the player session to be returned when found");
    }


}
