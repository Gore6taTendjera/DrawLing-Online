package com.example.drawling.application.controller;

import com.example.drawling.business.interfaces.service.BalanceService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/balances")
public class BalanceController {
    private final BalanceService balanceService;

    public BalanceController(BalanceService balanceService) {
        this.balanceService = balanceService;
    }

    @PreAuthorize("authentication.principal.userId == #userId")
    @GetMapping("/{userId}")
    public ResponseEntity<Double> getBalanceByUserId(@PathVariable int userId) {
        Double balance = balanceService.getBalanceByUserId(userId);
        return ResponseEntity.ok(balance);
    }

    @PreAuthorize("authentication.principal.userId == #userId")
    @PatchMapping("/{userId}/set")
    public ResponseEntity<String> setBalance(@PathVariable int userId, @RequestParam double amount) {
        int rowsUpdated = balanceService.setBalance(userId, amount);
        if (rowsUpdated > 0) {
            return ResponseEntity.ok("Balance updated successfully");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PreAuthorize("authentication.principal.userId == #userId")
    @PatchMapping("/{userId}/add")
    public ResponseEntity<String> addBalance(@PathVariable int userId, @RequestParam double amount) {
        int rowsUpdated = balanceService.addBalance(userId, amount);
        if (rowsUpdated > 0) {
            return ResponseEntity.ok("Balance updated successfully");
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
