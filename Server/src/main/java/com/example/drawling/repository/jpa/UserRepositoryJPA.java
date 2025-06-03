package com.example.drawling.repository.jpa;

import com.example.drawling.domain.entity.UserEntity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface UserRepositoryJPA extends CrudRepository<UserEntity, Integer> {
    @Transactional(readOnly = true)
    Optional<UserEntity> findByUsername(String username);

    @Transactional(readOnly = true)
    Optional<UserEntity> findByDisplayName(String displayName);

    @Transactional(readOnly = true)
    Optional<UserEntity> getByUsernameAndPassword(String username, String password);

    @Transactional(readOnly = true)
    @Query("SELECT u.displayName FROM UserEntity u WHERE u.id = :userId")
    Optional<String> findDisplayNameById(Integer userId);

    @Modifying
    @Transactional
    @Query("UPDATE UserEntity u SET u.displayName = :displayName WHERE u.id = :userId")
    int setDisplayName(@Param("userId") int userId, @Param("displayName") String displayName);

    @Transactional(readOnly = true)
    @Query("SELECT COUNT(u) FROM UserEntity u")
    int getTotalCount();
}
