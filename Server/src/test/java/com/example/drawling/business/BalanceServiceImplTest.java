package com.example.drawling.business;

import com.example.drawling.business.implementation.BalanceServiceImpl;
import com.example.drawling.business.interfaces.repository.BalanceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BalanceServiceImplTest {

    @Mock
    private BalanceRepository balanceRepository;

    @InjectMocks
    private BalanceServiceImpl balanceService;



    @Test
    void addBalance_ShouldCallRepository_WhenInputsAreValid() {
        // Arrange
        int userId = 1;
        double amount = 50.0;

        // Act
        balanceService.addBalance(userId, amount);

        // Assert
        verify(balanceRepository, times(1)).addBalance(userId, amount);
    }
    @Test
    void setBalance_ShouldCallRepository_WhenInputsAreValid() {
        // Arrange
        int userId = 1;
        double amount = 100.0;

        when(balanceRepository.setBalance(userId, amount)).thenReturn(100);

        // Act
        int balance = balanceService.setBalance(userId, amount);

        // Assert
        assertEquals(100, balance);
        verify(balanceRepository, times(1)).setBalance(userId, amount);
    }


    @Test
    void getBalanceByUserId_ShouldReturnBalance_WhenUserIdIsValid() {
        // Arrange
        int userId = 1;
        double expectedBalance = 100.0;

        when(balanceRepository.getBalanceByUserId(userId)).thenReturn(expectedBalance);

        // Act
        double balance = balanceService.getBalanceByUserId(userId);

        // Assert
        assertEquals(expectedBalance, balance);
        verify(balanceRepository, times(1)).getBalanceByUserId(userId);
    }

    @Test
    void setBalance_ShouldThrowException_WhenAmountIsNonPositive() {
        // Arrange
        int userId = 1;
        double amount = -50.0;

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                balanceService.setBalance(userId, amount));
        assertEquals("Balance amount must be greater than zero.", exception.getMessage());
        verify(balanceRepository, never()).setBalance(anyInt(), anyDouble());
    }

    @Test
    void setBalance_ShouldThrowException_WhenUserIdIsInvalid() {
        // Arrange
        int userId = -1;
        double amount = 150.0;

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                balanceService.setBalance(userId, amount));
        assertEquals("User id must be greater than zero.", exception.getMessage());
        verify(balanceRepository, never()).setBalance(anyInt(), anyDouble());
    }

    @Test
    void addBalance_ShouldThrowException_WhenAmountIsNonPositive() {
        // Arrange
        int userId = 1;
        double amount = -50.0;

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                balanceService.addBalance(userId, amount));
        assertEquals("Balance amount must be greater than zero.", exception.getMessage());
        verify(balanceRepository, never()).addBalance(anyInt(), anyDouble());
    }

    @Test
    void addBalance_ShouldThrowException_WhenUserIdIsInvalid() {
        // Arrange
        int userId = 0;
        double amount = 50.0;

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                balanceService.addBalance(userId, amount));
        assertEquals("User id must be greater than zero.", exception.getMessage());
        verify(balanceRepository, never()).addBalance(anyInt(), anyDouble());
    }

    @Test
    void getBalanceByUserId_ShouldThrowException_WhenUserIdIsInvalid() {
        // Arrange
        int userId = -1;

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                balanceService.getBalanceByUserId(userId));
        assertEquals("User id must be greater than zero.", exception.getMessage());
        verify(balanceRepository, never()).getBalanceByUserId(anyInt());
    }

    @Test
    void getBalanceByUserId_ShouldThrowException_WhenRepositoryThrowsException() {
        // Arrange
        int userId = 1;
        when(balanceRepository.getBalanceByUserId(userId)).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                balanceService.getBalanceByUserId(userId));
        assertEquals("Database error", exception.getMessage());
        verify(balanceRepository, times(1)).getBalanceByUserId(userId);
    }

    @Test
    void setBalance_ShouldThrowException_WhenRepositoryThrowsException() {
        // Arrange
        int userId = 1;
        double amount = 100.0;
        when(balanceRepository.setBalance(userId, amount)).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                balanceService.setBalance(userId, amount));
        assertEquals("Database error", exception.getMessage());
        verify(balanceRepository, times(1)).setBalance(userId, amount);
    }

    @Test
    void addBalance_ShouldThrowException_WhenRepositoryThrowsException() {
        // Arrange
        int userId = 1;
        double amount = 50.0;
        when(balanceRepository.addBalance(userId, amount)).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                balanceService.addBalance(userId, amount));
        assertEquals("Database error", exception.getMessage());
        verify(balanceRepository, times(1)).addBalance(userId, amount);
    }
}