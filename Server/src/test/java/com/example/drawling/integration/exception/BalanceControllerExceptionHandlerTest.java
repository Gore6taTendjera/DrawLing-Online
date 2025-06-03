package com.example.drawling.integration.exception;

import com.example.drawling.application.handler.exception.BalanceControllerExceptionHandler;
import com.example.drawling.exception.BalanceUpdateException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class BalanceControllerExceptionHandlerTest {

    @InjectMocks
    private BalanceControllerExceptionHandler exceptionHandler;

    @Mock
    private BalanceUpdateException balanceUpdateException;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testHandleBalanceUpdateException() {
        // Arrange
        String expectedMessage = "Failed to update balance.";
        when(balanceUpdateException.getMessage()).thenReturn(expectedMessage);

        // Act
        ResponseEntity<String> response = exceptionHandler.handleBalanceUpdateException(balanceUpdateException);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(expectedMessage, response.getBody());

        verify(balanceUpdateException, times(1)).getMessage();
    }
}
