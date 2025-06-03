package com.example.drawling.business.factory.game;

import com.example.drawling.business.implementation.game.mode.GameModeCombinedServiceImpl;
import com.example.drawling.business.implementation.game.mode.GameModeDuoServiceImpl;
import com.example.drawling.business.implementation.game.mode.GameModeNormalServiceImpl;
import com.example.drawling.business.implementation.game.mode.GameModeTrioServiceImpl;
import com.example.drawling.business.interfaces.factory.game.GameModeServiceFactory;
import com.example.drawling.business.interfaces.factory.game.GameRoundServiceFactory;
import com.example.drawling.business.interfaces.service.game.GameModeService;
import com.example.drawling.domain.model.game.mode.*;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Each GameModeService can have its own services inside.
 */
@Service
public class GameModeServiceFactoryImpl implements GameModeServiceFactory {

    private final Map<Class<? extends GameModeService<?>>, Supplier<? extends GameModeService<?>>> serviceSuppliers = new HashMap<>();

    public GameModeServiceFactoryImpl(GameRoundServiceFactory gameRoundServiceFactory) {
        serviceSuppliers.put(GameModeNormalServiceImpl.class, () -> new GameModeNormalServiceImpl(gameRoundServiceFactory));
        serviceSuppliers.put(GameModeDuoServiceImpl.class, () -> new GameModeDuoServiceImpl(gameRoundServiceFactory));
        serviceSuppliers.put(GameModeTrioServiceImpl.class, () -> new GameModeTrioServiceImpl(gameRoundServiceFactory));
        serviceSuppliers.put(GameModeCombinedServiceImpl.class, () -> new GameModeCombinedServiceImpl(gameRoundServiceFactory));
    }

    @Override
    public <T extends GameModeService<?>> T createGameModeService(Class<T> gameModeServiceClass) {
        Supplier<? extends GameModeService<?>> supplier = serviceSuppliers.get(gameModeServiceClass);
        if (supplier != null) {
            return getService(supplier);
        } else {
            throw new IllegalArgumentException("Unknown game mode service: " + gameModeServiceClass.getName());
        }
    }

    @Override
    public GameModeService<?> createGameModeService(GameMode gameMode) {
        return switch (gameMode) {
            case GameModeNormal gameModeNormal -> createGameModeService(GameModeNormalServiceImpl.class);
            case GameModeDuo gameModeDuo -> createGameModeService(GameModeDuoServiceImpl.class);
            case GameModeTrio gameModeTrio -> createGameModeService(GameModeTrioServiceImpl.class);
            case GameModeCombined gameModeCombined -> createGameModeService(GameModeCombinedServiceImpl.class);
            case null, default ->
                    throw new IllegalArgumentException("Unknown game mode");
        };
    }

    @SuppressWarnings("unchecked")
    private <T extends GameModeService<?>> T getService(Supplier<? extends GameModeService<?>> supplier) {
        return (T) supplier.get();
    }
}
