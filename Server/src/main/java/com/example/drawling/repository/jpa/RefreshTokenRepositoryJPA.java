package com.example.drawling.repository.jpa;

import com.example.drawling.domain.entity.RefreshTokenEntity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

public interface RefreshTokenRepositoryJPA extends CrudRepository<RefreshTokenEntity, Integer> {
    @Transactional(readOnly = true)
    @Query("SELECT r FROM RefreshTokenEntity r WHERE r.token = :token")
    Optional<RefreshTokenEntity> findByToken(String token);

    @Transactional(readOnly = true)
    @Query("SELECT r FROM RefreshTokenEntity r WHERE r.user.id = :userId")
    Optional<RefreshTokenEntity> findByUserId(int userId);

    @Modifying
    @Query("DELETE FROM RefreshTokenEntity r WHERE r.token = :token")
    int deleteByTokenId(String token);

    @Transactional(readOnly = true)
    @Query("SELECT r.expiry FROM RefreshTokenEntity r JOIN r.user u WHERE u.username = :username")
    Optional<Instant> getExpiryByUsername(String username);
}
