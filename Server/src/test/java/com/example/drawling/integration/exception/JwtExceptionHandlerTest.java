package com.example.drawling.integration.exception;

import com.example.drawling.application.handler.exception.JwtExceptionHandler;
import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JwtExceptionHandlerTest {

    @InjectMocks
    private JwtExceptionHandler jwtExceptionHandler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testHandleExpiredJwtException() {
        // Arrange
        String expectedMessage = "Your session has expired. Please log in again.";

        // Act
        ResponseEntity<String> response = jwtExceptionHandler.handleExpiredJwtException(new ExpiredJwtException(null, null, null));

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals(expectedMessage, response.getBody());
    }
}
