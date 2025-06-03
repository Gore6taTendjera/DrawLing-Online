package com.example.drawling.integration.handler;

import com.example.drawling.application.handler.PlayerHandler;
import com.example.drawling.business.implementation.game.CanvasImageConvertorServiceImpl;
import com.example.drawling.business.interfaces.service.BalanceService;
import com.example.drawling.business.interfaces.service.ExperienceLevelService;
import com.example.drawling.business.interfaces.service.ProfilePictureService;
import com.example.drawling.business.interfaces.service.UserSavedImageService;
import com.example.drawling.business.interfaces.service.game.GameSessionService;
import com.example.drawling.domain.model.Image;
import com.example.drawling.domain.model.Player;
import com.example.drawling.domain.model.game.GameSession;
import com.example.drawling.domain.model.game.PlayerSession;
import com.example.drawling.domain.model.game.canvas.CanvasEvent;
import com.example.drawling.domain.model.game.mode.GameMode;
import com.example.drawling.domain.model.game.round.GameRound;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.example.drawling.constants.MyConstants.API_IMAGES_URL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class PlayerHandlerTest {

    @Mock
    private BalanceService balanceService;

    @Mock
    private ExperienceLevelService experienceLevelService;

    @Mock
    private ProfilePictureService profilePictureService;

    @Mock
    private GameSessionService gameSessionService;

    @Mock
    private CanvasImageConvertorServiceImpl canvasImageConvertorServiceImpl;

    @Mock
    private UserSavedImageService userSavedImageService;

    @InjectMocks
    private PlayerHandler playerHandler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testHandlePlayerJoinStats_NullPlayerSession() {
        playerHandler.handlePlayerJoinStats(null);
        verifyNoInteractions(balanceService, experienceLevelService, profilePictureService);
    }

    @Test
    void testHandlePlayerJoinStats_InvalidUserId() {
        PlayerSession playerSession = new PlayerSession("session1", new Player(-1, "Player1"));
        playerHandler.handlePlayerJoinStats(playerSession);
        verifyNoInteractions(balanceService, experienceLevelService, profilePictureService);
    }

    @Test
    void testHandlePlayerJoinStats_ValidUserId() {
        int userId = 1;
        Player player = new Player(userId, "Player1");
        PlayerSession playerSession = new PlayerSession("session1", player);
        Image profileImage = new Image(1, "profile");

        when(balanceService.getBalanceByUserId(userId)).thenReturn(100.0);
        when(experienceLevelService.getTotalExperienceByUserId(userId)).thenReturn(500);
        when(profilePictureService.getUserProfilePictureById(userId)).thenReturn(profileImage);

        playerHandler.handlePlayerJoinStats(playerSession);

        verify(balanceService).getBalanceByUserId(userId);
        verify(experienceLevelService).getTotalExperienceByUserId(userId);
        verify(profilePictureService).getUserProfilePictureById(userId);

        assertEquals(100.0, player.getBalance());
        assertEquals(500, player.getExperience());
        assertEquals(API_IMAGES_URL + "1", player.getProfilePictureUrl());
    }

    @Test
    void testHandlePlayerSaveImage_NullSessionId() {
        playerHandler.handlePlayerSaveImage(null);
        verifyNoInteractions(gameSessionService, canvasImageConvertorServiceImpl, userSavedImageService);
    }

    @Test
    void testHandlePlayerSaveImage_EmptySessionId() {
        playerHandler.handlePlayerSaveImage("");
        verifyNoInteractions(gameSessionService, canvasImageConvertorServiceImpl, userSavedImageService);
    }

    @Test
    void testHandlePlayerSaveImage_InvalidUserId() {
        Player player = new Player(-1, "Player1");
        PlayerSession playerSession = new PlayerSession("session1", player);

        when(gameSessionService.getPlayerBySessionId("session1")).thenReturn(playerSession);

        playerHandler.handlePlayerSaveImage("session1");

        verify(gameSessionService).getPlayerBySessionId("session1");
        verifyNoMoreInteractions(gameSessionService, canvasImageConvertorServiceImpl, userSavedImageService);
    }

    @Test
    void testHandlePlayerSaveImage_ValidUserId() {
        int userId = 1;
        Player player = new Player(userId, "Player1");
        PlayerSession playerSession = new PlayerSession("session1", player);

        // Mock objects
        GameSession gameSession = mock(GameSession.class);
        GameMode gameMode = mock(GameMode.class);
        GameRound gameRound = mock(GameRound.class);
        LinkedList<GameRound> gameRounds = new LinkedList<>();
        gameRounds.add(gameRound);

        CopyOnWriteArrayList<CanvasEvent> canvasEvents = new CopyOnWriteArrayList<>();
        BufferedImage image = mock(BufferedImage.class);

        // Define behaviors for mocks
        when(gameSessionService.getPlayerBySessionId("session1")).thenReturn(playerSession);
        when(gameSessionService.getGameSessionByPlayerSessionId("session1")).thenReturn(gameSession);
        when(gameSession.getGameMode()).thenReturn(gameMode);
        when(gameMode.getGameRounds()).thenReturn(gameRounds);
        when(gameRound.getCanvasEvents()).thenReturn(canvasEvents);
        when(canvasImageConvertorServiceImpl.convertCanvasEventsToJpgImage(canvasEvents)).thenReturn(image);

        // Call the method under test
        playerHandler.handlePlayerSaveImage("session1");

        // Verify interactions
        verify(gameSessionService).getPlayerBySessionId("session1");
        verify(gameSessionService).getGameSessionByPlayerSessionId("session1");
        verify(gameSession).getGameMode();
        verify(gameMode).getGameRounds();
        verify(gameRound).getCanvasEvents();
        verify(canvasImageConvertorServiceImpl).convertCanvasEventsToJpgImage(canvasEvents);
        verify(userSavedImageService).saveUserDrawing(userId, image);
    }

}