package com.example.drawling.business;

import com.example.drawling.domain.enums.game.GameSessionState;
import com.example.drawling.domain.model.game.GameSession;
import com.example.drawling.domain.model.game.mode.GameModeNormal;
import com.example.drawling.domain.model.game.round.GameRoundNormal;
import com.example.drawling.statics.GameStaticSessions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class GameStaticSessionsTest {

    private static final String TEST_LINK = "testLink";
    private static final String TEST_LINK_2 = "testLink2";

    private static final GameModeNormal TEST_GAME_MODE = new GameModeNormal(4, Collections.singletonList(new GameRoundNormal(60)));
    private static final GameSession TEST_SESSION = new GameSession(TEST_GAME_MODE, GameSessionState.ACTIVE);
    private static final GameSession TEST_SESSION_2 = new GameSession(TEST_GAME_MODE, GameSessionState.ACTIVE);

    @BeforeEach
    void setUp() {
        // Clear the static session list before each test
        GameStaticSessions.clearSessions();
        GameStaticSessions.addSession(TEST_LINK, TEST_SESSION); // Add initial session for consistency
    }

    @Test
    void testGetByLink() {
        GameSession result = GameStaticSessions.getByLink(TEST_LINK);
        assertEquals(TEST_SESSION, result, "The session returned should match the added session.");
    }

    @Test
    void testGetByLinkNonExistent() {
        GameSession result = GameStaticSessions.getByLink(TEST_LINK_2);
        assertNull(result, "Non-existent link should return null.");
    }

    @Test
    void testSessionExists() {
        assertTrue(GameStaticSessions.sessionExists(TEST_LINK), "Session should exist for the added link.");
        assertFalse(GameStaticSessions.sessionExists(TEST_LINK_2), "Session should not exist for a non-existent link.");
    }

    @Test
    void testAddSession() {
        GameStaticSessions.addSession(TEST_LINK_2, TEST_SESSION_2);
        assertEquals(TEST_SESSION_2, GameStaticSessions.getByLink(TEST_LINK_2), "Added session should be retrievable by its link.");
    }

    @Test
    void testOverwriteSession() {
        GameStaticSessions.addSession(TEST_LINK, TEST_SESSION_2);
        assertEquals(TEST_SESSION_2, GameStaticSessions.getByLink(TEST_LINK), "Adding a session with an existing link should overwrite the old session.");
    }

    @Test
    void testGetReadOnlySessions() {
        Map<String, GameSession> readOnlySessions = GameStaticSessions.getReadOnlySessions();
        assertEquals(1, readOnlySessions.size(), "Read-only session map should reflect the current state.");
        assertEquals(TEST_SESSION, readOnlySessions.get(TEST_LINK), "The session in the read-only map should match the added session.");
    }

    @Test
    void testReadOnlySessionsImmutability() {
        Map<String, GameSession> readOnlySessions = GameStaticSessions.getReadOnlySessions();
        assertThrows(UnsupportedOperationException.class, () -> readOnlySessions.put(TEST_LINK_2, TEST_SESSION_2),
                "Read-only session map should not allow modifications.");
    }
}