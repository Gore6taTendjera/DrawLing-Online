package com.example.drawling.business.game.round;

import com.example.drawling.business.factory.game.GameRoundServiceFactoryImpl;
import com.example.drawling.business.implementation.game.round.GameRoundFastServiceImpl;
import com.example.drawling.business.implementation.game.round.GameRoundNormalServiceImpl;
import com.example.drawling.business.interfaces.service.game.GameRoundService;
import com.example.drawling.domain.model.game.round.GameRound;
import com.example.drawling.domain.model.game.round.GameRoundFast;
import com.example.drawling.domain.model.game.round.GameRoundNormal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class GameRoundServiceFactoryImplTest {

    @InjectMocks
    private GameRoundServiceFactoryImpl gameRoundServiceFactory;

    @Test
    void testCreateGameRoundServiceWithNormalService() {
        GameRoundService service = gameRoundServiceFactory.createGameRoundService(new GameRoundNormalServiceImpl());
        assertNotNull(service);
        assertInstanceOf(GameRoundNormalServiceImpl.class, service);
    }

    @Test
    void testCreateGameRoundServiceWithFastService() {
        GameRoundService service = gameRoundServiceFactory.createGameRoundService(new GameRoundFastServiceImpl());
        assertNotNull(service);
        assertInstanceOf(GameRoundFastServiceImpl.class, service);
    }

    @Test
    void testCreateGameRoundServiceWithUnknownService() {
        assertThrows(IllegalArgumentException.class, () -> {
            gameRoundServiceFactory.createGameRoundService(mock(GameRoundService.class));
        });
    }

    @Test
    void testCreateGameRoundServiceFromModelWithNormalRound() {
        GameRoundService service = gameRoundServiceFactory.createGameRoundServiceFromModel(new GameRoundNormal(10));
        assertNotNull(service);
        assertInstanceOf(GameRoundNormalServiceImpl.class, service);
    }

    @Test
    void testCreateGameRoundServiceFromModelWithFastRound() {
        GameRoundService service = gameRoundServiceFactory.createGameRoundServiceFromModel(new GameRoundFast(10));
        assertNotNull(service);
        assertInstanceOf(GameRoundFastServiceImpl.class, service);
    }

    @Test
    void testCreateGameRoundServiceFromModelWithUnknownRound() {
        assertThrows(IllegalArgumentException.class, () -> {
            gameRoundServiceFactory.createGameRoundServiceFromModel(mock(GameRound.class));
        });
    }
}