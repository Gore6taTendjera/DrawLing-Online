package com.example.drawling.integration.exception;

import com.example.drawling.application.handler.exception.UserControllerExceptionHandler;
import com.example.drawling.exception.UserDisplayNameNotFoundException;
import com.example.drawling.exception.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class UserControllerExceptionHandlerTest {

    private UserControllerExceptionHandler exceptionHandler;

    @Mock
    private UserNotFoundException userNotFoundException;

    @Mock
    private UserDisplayNameNotFoundException userDisplayNameNotFoundException;

    @BeforeEach
    void setUp() {
        exceptionHandler = new UserControllerExceptionHandler();
    }

    @Test
    void testHandleUserNotFoundException() {
        // Given
        userNotFoundException = new UserNotFoundException("User not found");

        // When
        ResponseEntity<String> response = exceptionHandler.handleUserNotFoundException(userNotFoundException);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("User not found.", response.getBody());
    }

    @Test
    void testHandleDisplayNameNotFoundException() {
        // Given
        userDisplayNameNotFoundException = new UserDisplayNameNotFoundException("Display name not found");

        // When
        ResponseEntity<String> response = exceptionHandler.handleDisplayNameNotFoundException(userDisplayNameNotFoundException);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Display name not found.", response.getBody());
    }
}