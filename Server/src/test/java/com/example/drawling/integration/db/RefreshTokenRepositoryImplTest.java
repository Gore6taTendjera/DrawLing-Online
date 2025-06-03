package com.example.drawling.integration.db;

import com.example.drawling.domain.entity.RefreshTokenEntity;
import com.example.drawling.domain.model.RefreshToken;
import com.example.drawling.exception.FailedToDeleteRefreshTokenException;
import com.example.drawling.exception.FailedToGetRefreshTokenException;
import com.example.drawling.exception.FailedToSaveRefreshTokenException;
import com.example.drawling.exception.TokenNotFoundException;
import com.example.drawling.mapper.RefreshTokenMapper;
import com.example.drawling.repository.implementation.RefreshTokenRepositoryImpl;
import com.example.drawling.repository.jpa.RefreshTokenRepositoryJPA;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshTokenRepositoryImplTest {

    @Mock
    private RefreshTokenRepositoryJPA jpa;

    @Mock
    private RefreshTokenMapper mapper;

    @InjectMocks
    private RefreshTokenRepositoryImpl refreshTokenRepository;

    private RefreshToken refreshToken;
    private RefreshTokenEntity refreshTokenEntity;

    @BeforeEach
    void setUp() {
        refreshToken = new RefreshToken();
        refreshTokenEntity = new RefreshTokenEntity();
    }

    @Test
    void testSave() {
        when(mapper.toEntity(refreshToken)).thenReturn(refreshTokenEntity);
        when(jpa.save(refreshTokenEntity)).thenReturn(refreshTokenEntity);

        RefreshToken result = refreshTokenRepository.save(refreshToken);

        assertNotNull(result);
        verify(mapper).toEntity(refreshToken);
        verify(jpa).save(refreshTokenEntity);
    }

    @Test
    void testSaveThrowsException() {
        when(mapper.toEntity(refreshToken)).thenReturn(refreshTokenEntity);
        doThrow(new DataAccessException("Database error") {}).when(jpa).save(refreshTokenEntity);

        assertThrows(FailedToSaveRefreshTokenException.class, () -> refreshTokenRepository.save(refreshToken));
    }

    @Test
    void testGetByToken() {
        String token = "test-token";
        when(jpa.findByToken(token)).thenReturn(Optional.of(refreshTokenEntity));
        when(mapper.toModel(refreshTokenEntity)).thenReturn(refreshToken);

        RefreshToken result = refreshTokenRepository.getByToken(token);

        assertNotNull(result);
        verify(jpa).findByToken(token);
        verify(mapper).toModel(refreshTokenEntity);
    }

    @Test
    void testGetByTokenNotFound() {
        String token = "test-token";
        when(jpa.findByToken(token)).thenReturn(Optional.empty());

        assertThrows(TokenNotFoundException.class, () -> refreshTokenRepository.getByToken(token));
    }

    @Test
    void testGetByUserId() {
        int userId = 1;
        when(jpa.findByUserId(userId)).thenReturn(Optional.of(refreshTokenEntity));
        when(mapper.toModel(refreshTokenEntity)).thenReturn(refreshToken);

        Optional<RefreshToken> result = refreshTokenRepository.getByUserId(userId);

        assertTrue(result.isPresent());
        verify(jpa).findByUserId(userId);
        verify(mapper).toModel(refreshTokenEntity);
    }

    @Test
    void testGetByUserIdNotFound() {
        int userId = 1;
        when(jpa.findByUserId(userId)).thenReturn(Optional.empty());

        Optional<RefreshToken> result = refreshTokenRepository.getByUserId(userId);

        assertFalse(result.isPresent());
    }

    @Test
    void testDelete() {
        when(mapper.toEntity(refreshToken)).thenReturn(refreshTokenEntity);

        refreshTokenRepository.delete(refreshToken);

        verify(mapper).toEntity(refreshToken);
        verify(jpa).delete(refreshTokenEntity);
    }

    @Test
    void testDeleteThrowsException() {
        when(mapper.toEntity(refreshToken)).thenReturn(refreshTokenEntity);
        doThrow(new DataAccessException("Database error") {}).when(jpa).delete(refreshTokenEntity);

        assertThrows(FailedToDeleteRefreshTokenException.class, () -> refreshTokenRepository.delete(refreshToken));
    }

    @Test
    void testDeleteByToken() {
        String token = "test-token";
        when(jpa.deleteByTokenId(token)).thenReturn(1);

        refreshTokenRepository.deleteByToken(token);

        verify(jpa).deleteByTokenId(token);
    }

    @Test
    void testDeleteByTokenNotFound() {
        String token = "test-token";
        when(jpa.deleteByTokenId(token)).thenReturn(0);

        assertThrows(TokenNotFoundException.class, () -> refreshTokenRepository.deleteByToken(token));
    }

    @Test
    void testDeleteByTokenThrowsException() {
        String token = "test-token";
        doThrow(new DataAccessException("Database error") {}).when(jpa).deleteByTokenId(token);

        assertThrows(FailedToDeleteRefreshTokenException.class, () -> refreshTokenRepository.deleteByToken(token));
    }

    @Test
    void testGetByUserIdThrowsDataAccessException() {
        int userId = 1;
        when(jpa.findByUserId(userId)).thenThrow(new DataAccessException("Database error") {});

        FailedToGetRefreshTokenException exception = assertThrows(
                FailedToGetRefreshTokenException.class,
                () -> refreshTokenRepository.getByUserId(userId)
        );

        assertEquals("Failed to get the refresh token by user id: " + userId, exception.getMessage());
        verify(jpa).findByUserId(userId);
    }

    @Test
    void testGetByTokenThrowsDataAccessException() {
        String token = "test-token";
        when(jpa.findByToken(token)).thenThrow(new DataAccessException("Database error") {});

        FailedToGetRefreshTokenException exception = assertThrows(
                FailedToGetRefreshTokenException.class,
                () -> refreshTokenRepository.getByToken(token)
        );

        assertEquals("Failed to get the refresh token by token: " + token, exception.getMessage());
        verify(jpa).findByToken(token);
    }


    @Test
    void testGetExpiryByUsernameThrowsDataAccessException() {
        String username = "test-user";
        when(jpa.getExpiryByUsername(username)).thenThrow(new DataAccessException("Database error") {});

        FailedToGetRefreshTokenException exception = assertThrows(
                FailedToGetRefreshTokenException.class,
                () -> refreshTokenRepository.getExpiryByUsername(username)
        );

        assertEquals("Failed to get the refresh token by username: " + username, exception.getMessage());
        verify(jpa).getExpiryByUsername(username);
    }

    @Test
    void testGetExpiryByUsernameThrowsTokenNotFoundException() {
        String username = "test-user";
        when(jpa.getExpiryByUsername(username)).thenReturn(Optional.empty());

        TokenNotFoundException exception = assertThrows(
                TokenNotFoundException.class,
                () -> refreshTokenRepository.getExpiryByUsername(username)
        );

        assertEquals("Refresh token not found: " + username, exception.getMessage());
        verify(jpa).getExpiryByUsername(username);
    }

    @Test
    void testGetExpiryByUsernameSuccess() {
        String username = "test-user";
        Instant expiry = Instant.now();
        when(jpa.getExpiryByUsername(username)).thenReturn(Optional.of(expiry));

        Instant result = refreshTokenRepository.getExpiryByUsername(username);

        assertEquals(expiry, result);
        verify(jpa).getExpiryByUsername(username);
    }




}
