package com.example.drawling.business.game;

import com.example.drawling.business.implementation.game.GameEconomyServiceImpl;
import com.example.drawling.business.interfaces.service.BalanceService;
import com.example.drawling.business.interfaces.service.ExperienceLevelService;
import com.example.drawling.domain.model.Player;
import com.example.drawling.domain.model.game.PlayerSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;

class GameEconomyServiceImplTest {

    @InjectMocks
    private GameEconomyServiceImpl gameEconomyService;

    @Mock
    private BalanceService balanceService;

    @Mock
    private ExperienceLevelService experienceLevelService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testWordGuessedForNotLoggedInPlayer() {
        // Arrange
        Player player = new Player();
        player.setId(0);
        player.setBalance(100);
        player.setExperience(10);

        PlayerSession playerSession = new PlayerSession("session1", player);

        // Act
        gameEconomyService.wordGuessed(playerSession);

        // Assert
        verifyNoInteractions(balanceService, experienceLevelService);
        assert player.getBalance() == 150 : "Player balance should increase by 50.";
        assert player.getExperience() == 11 : "Player experience should increase by 1.";
    }

    @Test
    void testWordGuessedForLoggedInPlayer() {
        // Arrange
        Player player = new Player();
        player.setId(1);

        PlayerSession playerSession = new PlayerSession("session2", player);

        when(balanceService.getBalanceByUserId(1)).thenReturn(200.0);
        when(experienceLevelService.getTotalExperienceByUserId(1)).thenReturn(20);

        // Act
        gameEconomyService.wordGuessed(playerSession);

        // Assert
        verify(balanceService).setBalance(1, 250.0);
        verify(experienceLevelService).setExperienceLevelByUserId(1, 21);
    }

}

