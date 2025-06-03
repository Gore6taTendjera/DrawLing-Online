package com.example.drawling.repository.implementation;

import com.example.drawling.business.interfaces.repository.BalanceRepository;
import com.example.drawling.exception.BalanceRetrievalException;
import com.example.drawling.exception.BalanceUpdateException;
import com.example.drawling.repository.jpa.BalanceRepositoryJPA;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class BalanceRepositoryImpl implements BalanceRepository {

    private final BalanceRepositoryJPA jpa;

    public BalanceRepositoryImpl(BalanceRepositoryJPA jpa) {
        this.jpa = jpa;
    }

    @Transactional(readOnly = true)
    public double getBalanceByUserId(int userId) {
        try {
            return jpa.findBalanceByUserId(userId);
        } catch (DataAccessException e) {
            throw new BalanceRetrievalException("Error retrieving balance for user ID " + userId, e);
        }
    }

    @Transactional
    public int setBalance(int userId, double newBalance) {
        try {
            return jpa.setBalance(userId, newBalance);
        } catch (DataAccessException e) {
            throw new BalanceUpdateException("Error updating balance for user ID " + userId, e);
        }
    }

    @Transactional
    public int addBalance(int userId, double amount) {
        try {
            return jpa.addBalance(userId, amount);
        } catch (DataAccessException e) {
            throw new BalanceUpdateException("Error updating balance for user ID " + userId, e);
        }
    }
}