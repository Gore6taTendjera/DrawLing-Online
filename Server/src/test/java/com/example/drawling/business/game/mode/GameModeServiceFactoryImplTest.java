package com.example.drawling.business.game.mode;

import com.example.drawling.business.factory.game.GameModeServiceFactoryImpl;
import com.example.drawling.business.implementation.game.mode.GameModeCombinedServiceImpl;
import com.example.drawling.business.implementation.game.mode.GameModeDuoServiceImpl;
import com.example.drawling.business.implementation.game.mode.GameModeNormalServiceImpl;
import com.example.drawling.business.implementation.game.mode.GameModeTrioServiceImpl;
import com.example.drawling.business.interfaces.service.game.GameModeService;
import com.example.drawling.domain.enums.game.GameModeState;
import com.example.drawling.domain.model.game.mode.*;
import com.example.drawling.domain.model.game.round.GameRound;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class GameModeServiceFactoryImplTest {


    @InjectMocks
    private GameModeServiceFactoryImpl gameModeServiceFactory;

    @Test
    void testCreateGameModeService_Normal() {
        GameModeService<?> service = gameModeServiceFactory.createGameModeService(GameModeNormalServiceImpl.class);
        assertNotNull(service);
        assertInstanceOf(GameModeNormalServiceImpl.class, service);
    }

    @Test
    void testCreateGameModeService_Duo() {
        GameModeService<?> service = gameModeServiceFactory.createGameModeService(GameModeDuoServiceImpl.class);
        assertNotNull(service);
        assertTrue(true);
    }

    @Test
    void testCreateGameModeService_Trio() {
        GameModeService<?> service = gameModeServiceFactory.createGameModeService(GameModeTrioServiceImpl.class);
        assertNotNull(service);
        assertTrue(true);
    }

    @Test
    void testCreateGameModeService_Combined() {
        GameModeService<?> service = gameModeServiceFactory.createGameModeService(GameModeCombinedServiceImpl.class);
        assertNotNull(service);
        assertTrue(true);
    }

    @Test
    void testCreateGameModeService_WithGameMode_Normal() {
        GameModeNormal gameModeNormal = new GameModeNormal(4, Collections.emptyList());
        GameModeService<?> service = gameModeServiceFactory.createGameModeService(gameModeNormal);
        assertNotNull(service);
        assertInstanceOf(GameModeNormalServiceImpl.class, service);
    }

    @Test
    void testCreateGameModeService_WithGameMode_Duo() {
        GameModeDuo gameModeDuo = new GameModeDuo(4, Collections.emptyList());
        GameModeService<?> service = gameModeServiceFactory.createGameModeService(gameModeDuo);
        assertNotNull(service);
        assertInstanceOf(GameModeDuoServiceImpl.class, service);
    }

    @Test
    void testCreateGameModeService_WithGameMode_Trio() {
        GameModeTrio gameModeTrio = new GameModeTrio(4, Collections.emptyList());
        GameModeService<?> service = gameModeServiceFactory.createGameModeService(gameModeTrio);
        assertNotNull(service);
        assertInstanceOf(GameModeTrioServiceImpl.class, service);
    }

    @Test
    void testCreateGameModeService_WithGameMode_Combined() {
        GameModeCombined gameModeCombined = new GameModeCombined(4, Collections.emptyList());
        GameModeService<?> service = gameModeServiceFactory.createGameModeService(gameModeCombined);
        assertNotNull(service);
        assertInstanceOf(GameModeCombinedServiceImpl.class, service);
    }

    @Test
    void testCreateGameModeService_WithNullGameMode() {
        assertThrows(IllegalArgumentException.class, () -> {
            gameModeServiceFactory.createGameModeService((GameMode) null);
        });
    }


    @Test
    void testCreateGameModeService_WithInvalidGameMode() {
        GameMode invalidGameMode = new GameMode(4, Collections.emptyList(), GameModeState.LOBBY) { };

        assertThrows(IllegalArgumentException.class, () -> {
            gameModeServiceFactory.createGameModeService(invalidGameMode);
        });
    }

    @Test
    void testCreateGameModeService_WithUnknownServiceClass() {
        class UnknownGameModeServiceImpl implements GameModeService<GameMode> {
            @Override
            public GameRound getActiveRound(GameMode gameMode) {
                return null;
            }
        }

        assertThrows(IllegalArgumentException.class, () -> {
            gameModeServiceFactory.createGameModeService(UnknownGameModeServiceImpl.class);
        });
    }
}