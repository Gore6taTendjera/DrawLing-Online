package com.example.drawling.integration.api.game.helper;

import com.example.drawling.application.controller.helper.GameRoundControllerHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class GameRoundControllerHelperTest {

    @InjectMocks
    private GameRoundControllerHelper gameRoundControllerHelper;

    @Mock
    private SimpMessagingTemplate simpMessagingTemplate;

    @BeforeEach
    void setUp() {
        // mock
    }

    @Test
    void testSendRoundNumberForRoom() {
        // Arrange
        String roomId = "testRoom";
        int roundNumber = 5;

        // Act
        gameRoundControllerHelper.sendRoundNumberForRoom(roomId, roundNumber);

        // Assert
        verify(simpMessagingTemplate, times(1))
                .convertAndSend("/topic/room/" + roomId + "/receive/roundNumber", roundNumber);
    }
}
