package com.example.drawling.business.implementation;

import com.example.drawling.business.interfaces.repository.RefreshTokenRepository;
import com.example.drawling.business.interfaces.service.RefreshTokenService;
import com.example.drawling.business.interfaces.service.UserService;
import com.example.drawling.domain.model.RefreshToken;
import com.example.drawling.domain.model.User;
import com.example.drawling.exception.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {

    @Value("${refreshToken.expiration}")
    private long tokenExpiryDuration;

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserService userService;

    public RefreshTokenServiceImpl(RefreshTokenRepository refreshTokenRepository, UserService userService) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userService = userService;
    }

    public RefreshToken createRefreshToken(String username) {
        User user = userService.getByUsername(username);
        RefreshToken existingToken = refreshTokenRepository.getByUserId(user.getId()).orElse(null);

        try {
            if (existingToken == null) {
                return createNewRefreshToken(user);
            }

            if (tokenExpired(existingToken)) {
                delete(existingToken);
                return createNewRefreshToken(user);
            }

            return existingToken;
        } catch (Exception e) {
            throw new RefreshTokenCreationException("Error creating refresh token for user " + username, e);
        }
    }


    public RefreshToken getByToken(String token) {
        try {
            RefreshToken refreshToken = refreshTokenRepository.getByToken(token);
            if (refreshToken == null) {
                throw new RefreshTokenNotFoundException("Refresh token not found for token: " + token);
            }
            return refreshToken;
        } catch (Exception e) {
            throw new RefreshTokenNotFoundException("Error retrieving refresh token for token: " + token);
        }
    }

    @Override
    public Instant getExpiryByUsername(String username) {
        return refreshTokenRepository.getExpiryByUsername(username);
    }


    public void verifyExpiration(RefreshToken token) {
        if (token == null) {
            throw new IllegalArgumentException("Token cannot be null.");
        }

        try {
            if (tokenExpired(token)) {
                delete(token);
                throw new RefreshTokenExpiredException("Refresh token has expired. Please login.");
            }
        } catch (Exception e) {
            throw new RefreshTokenExpirationCheckException("An error occurred while verifying the expiration of the refresh token.", e);
        }
    }

    public void delete(RefreshToken token) {
        try {
            refreshTokenRepository.delete(token);
        } catch (Exception e) {
            throw new RefreshTokenDeletionException("Error deleting refresh token for token: " + token.getToken(), e);
        }
    }

    public void deleteByToken(String token) {
        try {
            refreshTokenRepository.deleteByToken(token);
        } catch (Exception e) {
            throw new RefreshTokenDeletionException("Error deleting refresh token for token: " + token, e);
        }
    }

    public boolean tokenExpired(RefreshToken token) {
        try {
            return token.getExpiry().isBefore(Instant.now());
        } catch (Exception e) {
            throw new RefreshTokenExpirationCheckException("Error checking if refresh token has expired for token: " + token.getToken(), e);
        }
    }

    public RefreshToken createNewRefreshToken(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null.");
        }

        try {
            RefreshToken refreshToken = new RefreshToken();
            refreshToken.setToken(UUID.randomUUID().toString());
            refreshToken.setUser(user);
            refreshToken.setExpiry(Instant.now().plusSeconds(tokenExpiryDuration));
            return refreshTokenRepository.save(refreshToken);
        } catch (Exception e) {
            throw new RefreshTokenCreationException("Error creating refresh token for user: " + user.getUsername(), e);
        }
    }
}
