package com.example.drawling.business.interfaces.repository;

import com.example.drawling.domain.model.RefreshToken;

import java.time.Instant;
import java.util.Optional;

public interface RefreshTokenRepository {
    RefreshToken save(RefreshToken refreshToken);
    RefreshToken getByToken(String token);

    Optional<RefreshToken> getByUserId(int userId); // needs to be optional
    Instant getExpiryByUsername(String username);
    void delete(RefreshToken token);
    void deleteByToken(String token);
}
