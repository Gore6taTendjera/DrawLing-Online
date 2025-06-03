package com.example.drawling.integration.api;

import com.example.drawling.application.controller.BalanceController;
import com.example.drawling.business.interfaces.service.BalanceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class BalanceControllerTest {
    @Mock
    private BalanceService balanceService;

    @InjectMocks
    private BalanceController balanceController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @WithMockUser(username = "user", authorities = "ROLE_USER")
    void testGetBalanceByUserId() {
        int userId = 1;
        double expectedBalance = 100.0;

        when(balanceService.getBalanceByUserId(userId)).thenReturn(expectedBalance);

        ResponseEntity<Double> response = balanceController.getBalanceByUserId(userId);

        assertEquals(ResponseEntity.ok(expectedBalance), response);
        verify(balanceService, times(1)).getBalanceByUserId(userId);
    }

    @Test
    @WithMockUser(username = "user", authorities = "ROLE_USER")
    void testSetBalance() {
        int userId = 1;
        double amount = 50.0;
        when(balanceService.setBalance(userId, amount)).thenReturn(1);

        ResponseEntity<String> response = balanceController.setBalance(userId, amount);

        assertEquals(ResponseEntity.ok("Balance updated successfully"), response);
        verify(balanceService, times(1)).setBalance(userId, amount);
    }

    @Test
    @WithMockUser(username = "user", authorities = "ROLE_USER")
    void testSetBalanceNotFound() {
        int userId = 1;
        double amount = 50.0;
        when(balanceService.setBalance(userId, amount)).thenReturn(0);

        ResponseEntity<String> response = balanceController.setBalance(userId, amount);

        assertEquals(ResponseEntity.notFound().build(), response);
        verify(balanceService, times(1)).setBalance(userId, amount);
    }

    @Test
    @WithMockUser(username = "user", authorities = "ROLE_USER")
    void testAddBalance() {
        int userId = 1;
        double amount = 50.0;
        when(balanceService.addBalance(userId, amount)).thenReturn(1);

        ResponseEntity<String> response = balanceController.addBalance(userId, amount);

        assertEquals(ResponseEntity.ok("Balance updated successfully"), response);
        verify(balanceService, times(1)).addBalance(userId, amount);
    }

    @Test
    @WithMockUser(username = "user", authorities = "ROLE_USER")
    void testAddBalanceNotFound() {
        int userId = 1;
        double amount = 50.0;
        when(balanceService.addBalance(userId, amount)).thenReturn(0);

        ResponseEntity<String> response = balanceController.addBalance(userId, amount);

        assertEquals(ResponseEntity.notFound().build(), response);
        verify(balanceService, times(1)).addBalance(userId, amount);
    }
}

