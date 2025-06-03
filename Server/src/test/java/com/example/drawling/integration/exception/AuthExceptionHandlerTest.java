package com.example.drawling.integration.exception;

import com.example.drawling.application.handler.exception.AuthExceptionHandler;
import com.example.drawling.exception.RefreshTokenExpiredException;
import com.example.drawling.exception.RefreshTokenNotMatchUserException;
import com.example.drawling.exception.TokenNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class AuthExceptionHandlerTest {

    @InjectMocks
    private AuthExceptionHandler exceptionHandler;

    @Mock
    private RefreshTokenExpiredException refreshTokenExpiredException;

    @Mock
    private RefreshTokenNotMatchUserException refreshTokenNotMatchUserException;

    @Mock
    private TokenNotFoundException tokenNotFoundException;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testHandleRefreshTokenExpiredException() {
        // Arrange
        String expectedMessage = "Refresh token has expired.";
        when(refreshTokenExpiredException.getMessage()).thenReturn(expectedMessage);

        // Act
        ResponseEntity<String> response = exceptionHandler.handleRefreshTokenExpiredException(refreshTokenExpiredException);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals(expectedMessage, response.getBody());

        verify(refreshTokenExpiredException, times(1)).getMessage();
    }

    @Test
    void testHandleRefreshTokenNotMatchUser() {
        // Arrange
        String expectedMessage = "Refresh token does not match the user.";
        when(refreshTokenNotMatchUserException.getMessage()).thenReturn(expectedMessage);

        // Act
        ResponseEntity<String> response = exceptionHandler.handleRefreshTokenNotMatchUser(refreshTokenNotMatchUserException);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals(expectedMessage, response.getBody());

        verify(refreshTokenNotMatchUserException, times(1)).getMessage();
    }

    @Test
    void testHandleTokenNotFoundException() {
        // Arrange
        String expectedMessage = "Token not found.";
        when(tokenNotFoundException.getMessage()).thenReturn(expectedMessage);

        // Act
        ResponseEntity<String> response = exceptionHandler.handleTokenNotFoundException(tokenNotFoundException);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(expectedMessage, response.getBody());

        verify(tokenNotFoundException, times(1)).getMessage();
    }
}
