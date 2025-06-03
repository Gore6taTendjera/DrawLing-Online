package com.example.drawling.repository.jpa;

import com.example.drawling.domain.entity.UserEntity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface BalanceRepositoryJPA extends CrudRepository<UserEntity, Integer> {

    @Transactional(readOnly = true)
    @Query("SELECT u.balance FROM UserEntity u WHERE u.id = :userId")
    double findBalanceByUserId(@Param("userId") int userId);

    @Modifying
    @Transactional
    @Query("UPDATE UserEntity u SET u.balance = :newBalance WHERE u.id = :userId")
    int setBalance(@Param("userId") int userId, @Param("newBalance") double newBalance);

    @Modifying
    @Transactional
    @Query("UPDATE UserEntity u SET u.balance = u.balance + :amount WHERE u.id = :userId")
    int addBalance(@Param("userId") int userId, @Param("amount") double amount);

}
