package com.example.drawling.business.interfaces.service;

import com.example.drawling.domain.model.RefreshToken;

import java.time.Instant;

public interface RefreshTokenService {
   RefreshToken createRefreshToken(String username);
   RefreshToken getByToken(String token);
   Instant getExpiryByUsername(String username);
   void verifyExpiration(RefreshToken token);
   void delete(RefreshToken token);
   void deleteByToken(String token);
}
