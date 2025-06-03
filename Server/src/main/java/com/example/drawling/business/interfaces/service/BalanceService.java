package com.example.drawling.business.interfaces.service;


public interface BalanceService {

    double getBalanceByUserId(int userId);
    int setBalance(int userId, double newBalance);
    int addBalance(int userId, double amount);
}
