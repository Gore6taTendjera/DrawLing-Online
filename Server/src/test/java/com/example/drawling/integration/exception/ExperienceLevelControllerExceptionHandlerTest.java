package com.example.drawling.integration.exception;

import com.example.drawling.application.handler.exception.ExperienceLevelControllerExceptionHandler;
import com.example.drawling.exception.ExperienceLevelUpdateException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ExperienceLevelControllerExceptionHandlerTest {

    @InjectMocks
    private ExperienceLevelControllerExceptionHandler exceptionHandler;

    @Mock
    private ExperienceLevelUpdateException experienceLevelUpdateException;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testHandleExperienceLevelUpdateException() {
        // Arrange
        String expectedMessage = "Experience level update failed.";
        when(experienceLevelUpdateException.getMessage()).thenReturn(expectedMessage);

        // Act
        ResponseEntity<String> response = exceptionHandler.handleExperienceLevelUpdateException(experienceLevelUpdateException);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(expectedMessage, response.getBody());

        verify(experienceLevelUpdateException, times(1)).getMessage();
    }
}
