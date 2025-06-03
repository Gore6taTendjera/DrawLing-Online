package com.example.drawling.integration.api.game.helper;

import com.example.drawling.application.controller.helper.GameWordControllerHelper;
import com.example.drawling.domain.enums.game.GamePlayerRole;
import com.example.drawling.domain.model.Player;
import com.example.drawling.domain.model.game.GameSession;
import com.example.drawling.domain.model.game.PlayerSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GameWordControllerHelperTest {

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private GameWordControllerHelper gameWordControllerHelper;

    @Captor
    private ArgumentCaptor<String> destinationCaptor;

    @Captor
    private ArgumentCaptor<String> payloadCaptor;

    private GameSession gameSession;

    @BeforeEach
    void setUp() {
        gameSession = new GameSession();

        Map<String, PlayerSession> players = new HashMap<>();
        players.put("session1", new PlayerSession("session1", new Player("Player1")));
        players.put("session2", new PlayerSession("session2", new Player("Player2")));

        gameSession.setPlayers(players);

        gameSession.getPlayers().get("session1").setRole(GamePlayerRole.DRAWING);
        gameSession.getPlayers().get("session2").setRole(GamePlayerRole.GUESSING);
    }

    @Test
    void testSendWordToPlayers_DrawingPlayerReceivesWord() {
        // Arrange
        String word = "apple";

        PlayerSession drawingPlayer = new PlayerSession("session1", new Player("Player1", 100.0, "url1"));
        drawingPlayer.setRole(GamePlayerRole.DRAWING);

        PlayerSession guessingPlayer = new PlayerSession("session2", new Player("Player2", 200.0, "url2"));
        guessingPlayer.setRole(GamePlayerRole.GUESSING);

        Map<String, PlayerSession> players = Map.of(
                "session1", drawingPlayer,
                "session2", guessingPlayer
        );

        GameSession gameSession2 = mock(GameSession.class);

        when(gameSession2.getPlayers()).thenReturn(players);

        // Act
        gameWordControllerHelper.sendWordToPlayers(gameSession2, word);

        // Assert
        verify(messagingTemplate, times(2)).convertAndSendToUser(destinationCaptor.capture(), eq("topic/room/receive/word"), payloadCaptor.capture());

        int drawingPlayerIndex = destinationCaptor.getAllValues().indexOf("session1");
        assertEquals(word, payloadCaptor.getAllValues().get(drawingPlayerIndex));

        int guessingPlayerIndex = destinationCaptor.getAllValues().indexOf("session2");
        assertEquals("_ _ _ _ _", payloadCaptor.getAllValues().get(guessingPlayerIndex));
    }




    @Test
    void testSendWordToPlayers_GuessingPlayerReceivesUnderscores() {
        // Arrange
        String word = "banana";

        PlayerSession drawingPlayer = new PlayerSession("session1", new Player("Player1", 100.0, "url1"));
        drawingPlayer.setRole(GamePlayerRole.DRAWING);

        PlayerSession guessingPlayer = new PlayerSession("session2", new Player("Player2", 200.0, "url2"));
        guessingPlayer.setRole(GamePlayerRole.GUESSING);

        Map<String, PlayerSession> players = Map.of(
                "session1", drawingPlayer,
                "session2", guessingPlayer
        );

        GameSession gameSession = mock(GameSession.class);

        when(gameSession.getPlayers()).thenReturn(players);

        // Act
        gameWordControllerHelper.sendWordToPlayers(gameSession, word);

        // Assert
        ArgumentCaptor<String> userCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> payloadCaptor = ArgumentCaptor.forClass(String.class);

        verify(messagingTemplate, times(2)).convertAndSendToUser(userCaptor.capture(), eq("topic/room/receive/word"), payloadCaptor.capture());

        int drawingPlayerIndex = userCaptor.getAllValues().indexOf("session1");
        assertTrue(drawingPlayerIndex != -1, "session1 should be in the captured users list");
        assertEquals(word, payloadCaptor.getAllValues().get(drawingPlayerIndex), "Drawing player should receive the word");

        int guessingPlayerIndex = userCaptor.getAllValues().indexOf("session2");
        assertTrue(guessingPlayerIndex != -1, "session2 should be in the captured users list");
        String expectedUnderscores = IntStream.range(0, word.length()).mapToObj(i -> "_").collect(Collectors.joining(" "));
        assertEquals(expectedUnderscores, payloadCaptor.getAllValues().get(guessingPlayerIndex), "Guessing player should receive underscores");
    }


    @Test
    void testSendWordToPlayers_NoPlayers() {
        // Arrange
        GameSession emptyGameSession = new GameSession();
        emptyGameSession.setPlayers(new HashMap<>());

        // Act
        gameWordControllerHelper.sendWordToPlayers(emptyGameSession, "word");

        // Assert
        verifyNoInteractions(messagingTemplate);
    }

    @Test
    void testSendWordToPlayers_NullWord() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> gameWordControllerHelper.sendWordToPlayers(gameSession, null));
    }

    @Test
    void testSendWordToPlayers_NullGameSession() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> gameWordControllerHelper.sendWordToPlayers(null, "word"));
    }
}