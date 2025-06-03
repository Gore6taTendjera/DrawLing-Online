package com.example.drawling.business.implementation.game;

import com.example.drawling.business.interfaces.service.BalanceService;
import com.example.drawling.business.interfaces.service.ExperienceLevelService;
import com.example.drawling.domain.model.game.PlayerSession;
import org.springframework.stereotype.Service;

@Service
public class GameEconomyServiceImpl {
    private final BalanceService balanceService;
    private final ExperienceLevelService experienceLevelService;

    public static final int WORD_GUESS_REWARD_BALANCE = 50;
    public static final int WORD_GUESS_REWARD_EXPERIENCE = 1;


    public GameEconomyServiceImpl(BalanceService balanceService, ExperienceLevelService experienceLevelService) {
        this.balanceService = balanceService;
        this.experienceLevelService = experienceLevelService;
    }

    public void wordGuessed(PlayerSession playerSession) {
        if (playerSession == null) {
            throw new IllegalArgumentException("PlayerSession cannot be null");
        }

        int userId = playerSession.getPlayer().getId();

        playerSession.getPlayer().setBalance(playerSession.getPlayer().getBalance() + WORD_GUESS_REWARD_BALANCE);
        playerSession.getPlayer().setExperience(playerSession.getPlayer().getExperience() + WORD_GUESS_REWARD_EXPERIENCE);

        if (userId > 0) {
            // logged in player
            double balance = balanceService.getBalanceByUserId(userId);
            int exp = experienceLevelService.getTotalExperienceByUserId(userId);

            balanceService.setBalance(userId, balance + WORD_GUESS_REWARD_BALANCE);
            experienceLevelService.setExperienceLevelByUserId(userId, exp + WORD_GUESS_REWARD_EXPERIENCE);
        }
    }


}
