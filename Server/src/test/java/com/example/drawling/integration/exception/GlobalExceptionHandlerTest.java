package com.example.drawling.integration.exception;

import com.example.drawling.application.handler.exception.GlobalExceptionHandler;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testHandleEntityNotFound() {
        // Arrange
        String exceptionMessage = "Entity not found";
        EntityNotFoundException exception = new EntityNotFoundException(exceptionMessage);

        // Act
        ResponseEntity<String> response = globalExceptionHandler.handleEntityNotFound(exception);

        // Assert
        assertEquals(HttpStatus.NOT_EXTENDED, response.getStatusCode());
        assertEquals("GLOBAL HANDLER EntityNotFoundException: " + exceptionMessage, response.getBody());
    }

}
