package com.example.drawling.integration.api.game.helper;

import com.example.drawling.application.controller.helper.DrawingCanvasControllerHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class DrawingCanvasControllerHelperTest {

    @Mock
    private SimpMessagingTemplate simpMessagingTemplate;

    @InjectMocks
    private DrawingCanvasControllerHelper drawingCanvasControllerHelper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSendCanvasClear() {
        // Arrange
        String roomId = "testRoomId";

        // Act
        drawingCanvasControllerHelper.sendCanvasClear(roomId);

        // Assert
        verify(simpMessagingTemplate, times(1))
                .convertAndSend("/topic/canvas/receive/clear/" + roomId, "");
    }

    @Test
    void testSendCanvasClear_EmptyRoomId() {
        // Arrange
        String emptyRoomId = "";

        // Act
        drawingCanvasControllerHelper.sendCanvasClear(emptyRoomId);

        // Assert
        verify(simpMessagingTemplate, times(1))
                .convertAndSend("/topic/canvas/receive/clear/" + emptyRoomId, "");
    }

    @Test
    void testSendCanvasClear_NullRoomId() {
        // Act & Assert
        assertThrows(NullPointerException.class,
                () -> drawingCanvasControllerHelper.sendCanvasClear(null),
                "Expected NullPointerException for null roomId");
    }

}
