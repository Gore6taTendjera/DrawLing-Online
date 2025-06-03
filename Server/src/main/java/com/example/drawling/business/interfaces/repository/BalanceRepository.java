package com.example.drawling.business.interfaces.repository;

import org.springframework.stereotype.Repository;

@Repository
public interface BalanceRepository {
    double getBalanceByUserId(int userId);
    int setBalance(int userId, double newBalance);
    int addBalance(int userId, double amount);
}
