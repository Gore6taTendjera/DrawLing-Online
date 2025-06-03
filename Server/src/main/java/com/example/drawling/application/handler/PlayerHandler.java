package com.example.drawling.application.handler;

import com.example.drawling.business.implementation.game.CanvasImageConvertorServiceImpl;
import com.example.drawling.business.interfaces.service.BalanceService;
import com.example.drawling.business.interfaces.service.ExperienceLevelService;
import com.example.drawling.business.interfaces.service.ProfilePictureService;
import com.example.drawling.business.interfaces.service.UserSavedImageService;
import com.example.drawling.business.interfaces.service.game.GameSessionService;
import com.example.drawling.domain.model.game.GameSession;
import com.example.drawling.domain.model.game.PlayerSession;
import com.example.drawling.domain.model.game.canvas.CanvasEvent;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.example.drawling.constants.MyConstants.API_IMAGES_URL;

@Service
public class PlayerHandler {
    private final BalanceService balanceService;
    private final ExperienceLevelService experienceLevelService;
    private final ProfilePictureService profilePictureService;
    private final GameSessionService gameSessionService;
    private final CanvasImageConvertorServiceImpl canvasImageConvertorServiceImpl;
    private final UserSavedImageService userSavedImageService;

    public PlayerHandler(BalanceService balanceService, ExperienceLevelService experienceLevelService, ProfilePictureService profilePictureService, GameSessionService gameSessionService, CanvasImageConvertorServiceImpl canvasImageConvertorServiceImpl, UserSavedImageService userSavedImageService) {
        this.balanceService = balanceService;
        this.experienceLevelService = experienceLevelService;
        this.profilePictureService = profilePictureService;
        this.gameSessionService = gameSessionService;
        this.canvasImageConvertorServiceImpl = canvasImageConvertorServiceImpl;
        this.userSavedImageService = userSavedImageService;
    }


    // checks if the player is logged in and sets data from the database
    public void handlePlayerJoinStats(PlayerSession playerSession) {
        if (playerSession == null) {
            return;
        }
        int userId = playerSession.getPlayer().getId();
        if (userId <= 0) {
            return;
        }

        double balance = balanceService.getBalanceByUserId(userId);
        int exp = experienceLevelService.getTotalExperienceByUserId(userId);
        String url = API_IMAGES_URL + profilePictureService.getUserProfilePictureById(userId).getId();

        playerSession.getPlayer().setProfilePictureUrl(url);
        playerSession.getPlayer().setBalance(balance);
        playerSession.getPlayer().setExperience(exp);
    }


    public void handlePlayerSaveImage(String playerSessionId) {
        if (playerSessionId == null || playerSessionId.isEmpty()) {
            return;
        }

        PlayerSession playerSession = gameSessionService.getPlayerBySessionId(playerSessionId);
        int userId = playerSession.getPlayer().getId();

        if (userId <= 0) {
            return;
        }

        GameSession gs = gameSessionService.getGameSessionByPlayerSessionId(playerSessionId);
        List<CanvasEvent> canvasEvents = gs.getGameMode().getGameRounds().getFirst().getCanvasEvents();
        BufferedImage image = canvasImageConvertorServiceImpl.convertCanvasEventsToJpgImage(canvasEvents);
        userSavedImageService.saveUserDrawing(userId, image);
    }


    public String handleCheckPlayerNameDuplication(String roomId, String playerName) {
        playerName = playerName.trim();
        GameSession gs = gameSessionService.getGameSessionById(roomId);
        int maxCount = 0;

        for (PlayerSession playerSession : gameSessionService.getAllPlayers(gs)) {
            String existingPlayerName = playerSession.getPlayer().getDisplayName();

            String regex = "\\[(\\d+)]$";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(existingPlayerName);

            if (matcher.find()) {
                int number = Integer.parseInt(matcher.group(1));
                if (existingPlayerName.replace("[" + number + "]", "").equalsIgnoreCase(playerName) && number > maxCount) {
                    maxCount = number;
                }

            } else if (existingPlayerName.equalsIgnoreCase(playerName)) {
                maxCount = Math.max(maxCount, 1);
            }
        }

        if (maxCount > 0) {
            playerName += "[" + (maxCount + 1) + "]";
        }

        return playerName;
    }



}
