package com.example.drawling.application.controller.game;

import com.example.drawling.application.controller.helper.PlayerControllerHelper;
import com.example.drawling.business.implementation.game.GameEconomyServiceImpl;
import com.example.drawling.business.interfaces.service.ProfilePictureService;
import com.example.drawling.business.interfaces.service.game.GameSessionService;
import com.example.drawling.domain.dto.game.ChatMessageDTO;
import com.example.drawling.domain.enums.game.GamePlayerRole;
import com.example.drawling.domain.model.game.GameSession;
import com.example.drawling.domain.model.game.PlayerSession;
import com.example.drawling.domain.model.game.round.GameRound;
import com.example.drawling.statics.GameStaticSessions;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.RestController;

import static com.example.drawling.constants.MyConstants.API_IMAGES_URL;

@RestController
@EnableScheduling
public class ChatController {
    private final SimpMessagingTemplate messagingTemplate;
    private final GameSessionService gameSessionService;
    private final GameEconomyServiceImpl gameEconomyServiceImpl;
    private final PlayerControllerHelper playerControllerHelper;
    private final ProfilePictureService profilePictureService;

    public ChatController(SimpMessagingTemplate messagingTemplate, GameSessionService gameSessionService, GameEconomyServiceImpl gameEconomyServiceImpl, PlayerControllerHelper playerControllerHelper, ProfilePictureService profilePictureService) {
        this.messagingTemplate = messagingTemplate;
        this.gameSessionService = gameSessionService;
        this.gameEconomyServiceImpl = gameEconomyServiceImpl;
        this.playerControllerHelper = playerControllerHelper;
        this.profilePictureService = profilePictureService;
    }


    @MessageMapping("chat/send/{roomId}")
    public void chatMessage(@DestinationVariable String roomId, String msg, SimpMessageHeaderAccessor headerAccessor) {
        GameSession gameSession = GameStaticSessions.getByLink(roomId);
        PlayerSession player = gameSessionService.getPlayerBySessionAndPlayerSessionId(gameSession, headerAccessor.getSessionId());
        GamePlayerRole role = player.getRole();
        GameRound activeGameRound = gameSessionService.getActiveGameRound(gameSession);

        if (role == GamePlayerRole.GUESSING) {
            boolean guessed = msg.equalsIgnoreCase(activeGameRound.getWord());

            if (guessed) {
                gameEconomyServiceImpl.wordGuessed(player);
                playerControllerHelper.sendActivePlayers(roomId);
            } else {
                String imageUrl = null;
                try {
                    int imageId = profilePictureService.getUserProfilePictureIdByUserId(player.getPlayer().getId());
                    imageUrl = API_IMAGES_URL + imageId;
                } catch (Exception e) {
                    // image id is 0 for not logged in players
                    imageUrl = null;
                }
                messagingTemplate.convertAndSend("/topic/chat/receive/room/" + roomId, new ChatMessageDTO(player.getPlayer().getDisplayName(), msg, imageUrl));
            }
        }
    }



}


