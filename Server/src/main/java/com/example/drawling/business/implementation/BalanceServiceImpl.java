package com.example.drawling.business.implementation;

import com.example.drawling.business.interfaces.repository.BalanceRepository;
import com.example.drawling.business.interfaces.service.BalanceService;
import org.springframework.stereotype.Service;

@Service
public class BalanceServiceImpl implements BalanceService {
    private static final String USER_ID_MUST_BE_GREATER_THAN_ZERO = "User id must be greater than zero.";
    private final BalanceRepository balanceRepository;

    public BalanceServiceImpl(BalanceRepository balanceRepository) {
        this.balanceRepository = balanceRepository;
    }

    public double getBalanceByUserId(int userId) {
        if (userId <= 0) {
            throw new IllegalArgumentException(USER_ID_MUST_BE_GREATER_THAN_ZERO);
        }
        return balanceRepository.getBalanceByUserId(userId);
    }

    public int setBalance(int userId, double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Balance amount must be greater than zero.");
        }
        if (userId <= 0 ) {
            throw new IllegalArgumentException(USER_ID_MUST_BE_GREATER_THAN_ZERO);
        }

        return balanceRepository.setBalance(userId, amount);
    }

    public int addBalance(int userId, double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Balance amount must be greater than zero.");
        }
        if (userId <= 0 ) {
            throw new IllegalArgumentException(USER_ID_MUST_BE_GREATER_THAN_ZERO);
        }

        return balanceRepository.addBalance(userId, amount);
    }
}