package com.example.drawling.repository.jpa;

import com.example.drawling.domain.entity.UserEntity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface ExperienceLevelRepositoryJPA extends CrudRepository<UserEntity, Integer> {

    @Query("SELECT u.experience FROM UserEntity u WHERE u.id = :userId")
    int getTotalExperienceByUserId(@Param("userId") int userId);

    @Modifying
    @Transactional
    @Query("UPDATE UserEntity u SET u.experience = :newExperienceLevel WHERE u.id = :userId")
    int setExperienceLevel(@Param("userId") int userId, @Param("newExperienceLevel") double newExperienceLevel);

    @Modifying
    @Transactional
    @Query("UPDATE UserEntity u SET u.experience = u.experience + :amount WHERE u.id = :userId")
    int addExperienceLevel(@Param("userId") int userId, @Param("amount") double amount);

}
