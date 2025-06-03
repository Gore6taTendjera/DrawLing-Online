package com.example.drawling.integration.api.game;

import com.example.drawling.application.controller.helper.TimerControllerHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TimerControllerTest {

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    private TimerControllerHelper timerControllerHelper;

    @BeforeEach
    void setUp() {
        timerControllerHelper = new TimerControllerHelper(messagingTemplate);
    }


    @Test
    void sendToRoom_ShouldSendMessage() {
        String roomId = "room1";
        String timeMessage = "some time message";

        timerControllerHelper.sendToRoom(roomId, timeMessage);

        verify(messagingTemplate).convertAndSend("/topic/timer/" + roomId, timeMessage);

    }
}