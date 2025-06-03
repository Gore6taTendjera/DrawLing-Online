package com.example.drawling.integration.api.game;

import com.example.drawling.application.controller.game.DrawingCanvasController;
import com.example.drawling.application.handler.PlayerHandler;
import com.example.drawling.business.interfaces.service.game.GameSessionService;
import com.example.drawling.domain.model.game.GameSession;
import com.example.drawling.domain.model.game.PlayerSession;
import com.example.drawling.domain.model.game.canvas.CanvasClear;
import com.example.drawling.domain.model.game.canvas.CanvasDrawLine;
import com.example.drawling.domain.model.game.canvas.CanvasEvent;
import com.example.drawling.domain.model.game.mode.GameMode;
import com.example.drawling.domain.model.game.round.GameRound;
import com.example.drawling.domain.model.game.round.GameRoundNormal;
import com.example.drawling.statics.GameStaticSessions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DrawingCanvasControllerTest {

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @Mock
    private GameSessionService gameSessionService;

    @Mock
    private PlayerHandler playerHandler;

    @InjectMocks
    private DrawingCanvasController drawingCanvasController;

    private SimpMessageHeaderAccessor headerAccessor;
    private GameSession mockGameSession;
    private CopyOnWriteArrayList<CanvasEvent> mockCanvasEvents;
    private GameRoundNormal mockGameRound;
    private List<PlayerSession> mockPlayerSessions;

    @BeforeEach
    void setup() {
        headerAccessor = mock(SimpMessageHeaderAccessor.class);
        mockGameSession = mock(GameSession.class);
        mockCanvasEvents = mock(CopyOnWriteArrayList.class);
        mockPlayerSessions = new ArrayList<>();


        mockGameRound = mock(GameRoundNormal.class);

        lenient().when(headerAccessor.getSessionId()).thenReturn("mockSessionId");
        lenient().when(mockGameSession.getGameMode()).thenReturn(mock(GameMode.class));
        lenient().when(mockGameRound.getCanvasEvents()).thenReturn(mockCanvasEvents);
    }

    @Test
    void testClearCanvas() {
        String roomId = "testRoomId";

        LinkedList<GameRound> mockGameRounds = new LinkedList<>();
        mockGameRounds.add(mockGameRound);
        GameMode mockGameMode = mock(GameMode.class);

        when(mockGameSession.getGameMode()).thenReturn(mockGameMode);
        when(mockGameMode.getGameRounds()).thenReturn(mockGameRounds);

        try (MockedStatic<GameStaticSessions> mockedStatic = mockStatic(GameStaticSessions.class)) {
            mockedStatic.when(() -> GameStaticSessions.getByLink(roomId)).thenReturn(mockGameSession);

            drawingCanvasController.clearCanvas(roomId);

            verify(mockGameRound, times(1)).getCanvasEvents();
            verify(mockCanvasEvents, times(1)).add(any(CanvasClear.class));
        }
    }



    @Test
    void testReceiveCanvasData() {
        CanvasDrawLine mockCanvasDrawLine = mock(CanvasDrawLine.class);

        GameRoundNormal mockGameRound2 = mock(GameRoundNormal.class);

        CopyOnWriteArrayList<CanvasEvent> mockCanvasEvents2 = mock(CopyOnWriteArrayList.class);
        when(mockGameRound2.getCanvasEvents()).thenReturn(mockCanvasEvents2);

        LinkedList<GameRound> mockGameRounds = new LinkedList<>();
        mockGameRounds.add(mockGameRound2);

        GameMode mockGameMode = mock(GameMode.class);
        when(mockGameMode.getGameRounds()).thenReturn(mockGameRounds);

        when(gameSessionService.getGameSessionByPlayerSessionId(anyString())).thenReturn(mockGameSession);
        when(mockGameSession.getGameMode()).thenReturn(mockGameMode);
        when(gameSessionService.getAllPlayers(mockGameSession)).thenReturn(mockPlayerSessions);

        drawingCanvasController.receiveCanvasData(mockCanvasDrawLine, headerAccessor);

        verify(messagingTemplate, times(mockPlayerSessions.size()))
                .convertAndSendToUser(anyString(), eq("/topic/canvas/receive/draw"), eq(mockCanvasDrawLine));
    }


    @Test
    void testSaveCanvasImage() {
        doNothing().when(playerHandler).handlePlayerSaveImage(anyString());

        drawingCanvasController.saveCanvasImage(headerAccessor);

        verify(playerHandler).handlePlayerSaveImage("mockSessionId");
    }
}