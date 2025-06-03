package com.example.drawling.statics;

import com.example.drawling.domain.model.game.GameSession;

import java.util.HashMap;
import java.util.Map;

public class GameStaticSessions {
    private static Map<String, GameSession> gameSessionList = new HashMap<>();

    private GameStaticSessions() {}

    public static GameSession getByLink(String link) {
        return gameSessionList.get(link);
    }

    public static boolean sessionExists(String link) {
        return gameSessionList.containsKey(link);
    }

    public static void addSession(String uniqueId, GameSession gameSession) {
        gameSessionList.put(uniqueId, gameSession);
    }

    public static Map<String, GameSession> getReadOnlySessions() {
        return Map.copyOf(gameSessionList);
    }

    public static void clearSessions() {
        gameSessionList.clear();
    }

}
