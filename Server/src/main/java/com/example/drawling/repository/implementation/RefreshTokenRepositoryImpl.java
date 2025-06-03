package com.example.drawling.repository.implementation;

import com.example.drawling.business.interfaces.repository.RefreshTokenRepository;
import com.example.drawling.domain.entity.RefreshTokenEntity;
import com.example.drawling.domain.model.RefreshToken;
import com.example.drawling.exception.FailedToDeleteRefreshTokenException;
import com.example.drawling.exception.FailedToGetRefreshTokenException;
import com.example.drawling.exception.FailedToSaveRefreshTokenException;
import com.example.drawling.exception.TokenNotFoundException;
import com.example.drawling.mapper.RefreshTokenMapper;
import com.example.drawling.repository.jpa.RefreshTokenRepositoryJPA;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Repository
public class RefreshTokenRepositoryImpl implements RefreshTokenRepository {

    private final RefreshTokenRepositoryJPA jpa;
    private final RefreshTokenMapper mapper;

    public RefreshTokenRepositoryImpl(RefreshTokenRepositoryJPA refreshTokenRepositoryJPA, RefreshTokenMapper refreshTokenMapper) {
        this.jpa = refreshTokenRepositoryJPA;
        this.mapper = refreshTokenMapper;
    }

    @Transactional
    public RefreshToken save(RefreshToken refreshToken) {
        try {
            jpa.save(mapper.toEntity(refreshToken));
            return refreshToken;
        } catch (DataAccessException e) {
            throw new FailedToSaveRefreshTokenException("Failed to save the refresh token.", e);
        }
    }

    @Transactional(readOnly = true)
    public RefreshToken getByToken(String token) {
        try {
            Optional<RefreshTokenEntity> refreshToken = jpa.findByToken(token);
            return refreshToken
                    .map(mapper::toModel)
                    .orElseThrow(() -> new TokenNotFoundException("Refresh token not found: " + token));
        } catch (DataAccessException e) {
            throw new FailedToGetRefreshTokenException("Failed to get the refresh token by token: " + token, e);
        }
    }

    @Transactional(readOnly = true)
    public Optional<RefreshToken> getByUserId(int userId) {
        try {
            Optional<RefreshTokenEntity> refreshTokenEntity = jpa.findByUserId(userId);
            return refreshTokenEntity.map(mapper::toModel);
        } catch (DataAccessException e) {
            throw new FailedToGetRefreshTokenException("Failed to get the refresh token by user id: " + userId, e);
        }
    }

    @Transactional(readOnly = true)
    public Instant getExpiryByUsername(String username) {
        try {
            return jpa.getExpiryByUsername(username).orElseThrow(
                    () -> new TokenNotFoundException("Refresh token not found: " + username)
            );
        } catch (DataAccessException e) {
            throw new FailedToGetRefreshTokenException("Failed to get the refresh token by username: " + username, e);
        }
    }


    @Transactional
    public void delete(RefreshToken token) {
        try {
            jpa.delete(mapper.toEntity(token));
        } catch (DataAccessException e) {
            throw new FailedToDeleteRefreshTokenException("Failed to delete the refresh token.", e);
        }
    }

    @Transactional
    public void deleteByToken(String token) {
        try {
            int deletedCount = jpa.deleteByTokenId(token);
            if (deletedCount == 0) {
                throw new TokenNotFoundException("Token not found: " + token);
            }
        } catch (DataAccessException e) {
            throw new FailedToDeleteRefreshTokenException("Failed to delete the refresh token.", e);
        }
    }
}