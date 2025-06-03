package com.example.drawling.integration.db;

import com.example.drawling.exception.BalanceRetrievalException;
import com.example.drawling.exception.BalanceUpdateException;
import com.example.drawling.repository.implementation.BalanceRepositoryImpl;
import com.example.drawling.repository.jpa.BalanceRepositoryJPA;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataAccessException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class BalanceRepositoryImplTest {

    @Mock
    private BalanceRepositoryJPA jpa;

    @InjectMocks
    private BalanceRepositoryImpl balanceRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetBalanceByUserId_Success() {
        int userId = 1;
        double expectedBalance = 100.0;

        when(jpa.findBalanceByUserId(userId)).thenReturn(expectedBalance);

        double actualBalance = balanceRepository.getBalanceByUserId(userId);

        assertEquals(expectedBalance, actualBalance);
        verify(jpa, times(1)).findBalanceByUserId(userId);
    }

    @Test
    void testGetBalanceByUserId_DataAccessException() {
        int userId = 1;

        when(jpa.findBalanceByUserId(userId)).thenThrow(new DataAccessException("Database error") {});

        assertThrows(BalanceRetrievalException.class, () -> {
            balanceRepository.getBalanceByUserId(userId);
        });
    }

    @Test
    void testSetBalance_Success() {
        int userId = 1;
        double newBalance = 150.0;
        int rowsUpdated = 1;

        when(jpa.setBalance(userId, newBalance)).thenReturn(rowsUpdated);

        int actualRowsUpdated = balanceRepository.setBalance(userId, newBalance);

        assertEquals(rowsUpdated, actualRowsUpdated);
        verify(jpa, times(1)).setBalance(userId, newBalance);
    }

    @Test
    void testSetBalance_DataAccessException() {
        int userId = 1;
        double newBalance = 150.0;

        when(jpa.setBalance(userId, newBalance)).thenThrow(new DataAccessException("Database error") {});

        assertThrows(BalanceUpdateException.class, () -> {
            balanceRepository.setBalance(userId, newBalance);
        });
    }

    @Test
    void testAddBalance_Success() {
        int userId = 1;
        double amount = 50.0;
        int rowsUpdated = 1;

        when(jpa.addBalance(userId, amount)).thenReturn(rowsUpdated);

        int actualRowsUpdated = balanceRepository.addBalance(userId, amount);

        assertEquals(rowsUpdated, actualRowsUpdated);
        verify(jpa, times(1)).addBalance(userId, amount);
    }

    @Test
    void testAddBalance_DataAccessException() {
        int userId = 1;
        double amount = 50.0;

        when(jpa.addBalance(userId, amount)).thenThrow(new DataAccessException("Database error") {});

        assertThrows(BalanceUpdateException.class, () -> {
            balanceRepository.addBalance(userId, amount);
        });
    }
}
