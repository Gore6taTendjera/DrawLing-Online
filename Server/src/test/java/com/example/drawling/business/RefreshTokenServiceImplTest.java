package com.example.drawling.business;

import com.example.drawling.business.implementation.RefreshTokenServiceImpl;
import com.example.drawling.business.interfaces.repository.RefreshTokenRepository;
import com.example.drawling.business.interfaces.service.UserService;
import com.example.drawling.domain.model.RefreshToken;
import com.example.drawling.domain.model.User;
import com.example.drawling.exception.RefreshTokenCreationException;
import com.example.drawling.exception.RefreshTokenDeletionException;
import com.example.drawling.exception.RefreshTokenExpirationCheckException;
import com.example.drawling.exception.RefreshTokenNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceImplTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private RefreshTokenServiceImpl refreshTokenService;

    private User user;
    private RefreshToken refreshToken;

    @BeforeEach
    public void setUp() {
        user = new User();
        user.setId(1);
        user.setUsername("testUser");

        refreshToken = new RefreshToken();
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setUser(user);
        refreshToken.setExpiry(Instant.now().plusSeconds(3600)); // 1 hour expiry
    }

    @Test
    void testCreateRefreshToken_NewToken() {
        when(userService.getByUsername("testUser")).thenReturn(user);
        when(refreshTokenRepository.getByUserId(user.getId())).thenReturn(Optional.empty());
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(refreshToken);

        RefreshToken createdToken = refreshTokenService.createRefreshToken("testUser");

        assertNotNull(createdToken);
        assertEquals(user.getId(), createdToken.getUser().getId());
        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }

    @Test
    void testCreateRefreshToken_Exception() {
        String username = "testUser";

        when(userService.getByUsername(username)).thenThrow(new RuntimeException("Database error"));

        assertThrows(RuntimeException.class, () -> {
            refreshTokenService.createRefreshToken(username);
        });
    }


    @Test
    void testCreateRefreshToken_ExistingTokenNotExpired() {
        when(userService.getByUsername("testUser")).thenReturn(user);
        when(refreshTokenRepository.getByUserId(user.getId())).thenReturn(Optional.of(refreshToken));

        RefreshToken existingToken = refreshTokenService.createRefreshToken("testUser");

        assertNotNull(existingToken);
        assertEquals(refreshToken.getToken(), existingToken.getToken());
        verify(refreshTokenRepository, never()).save(any(RefreshToken.class));
    }


    @Test
    void testVerifyExpiration_NullToken() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            refreshTokenService.verifyExpiration(null);
        });

        assertEquals("Token cannot be null.", exception.getMessage());
    }

    @Test
    void testVerifyExpiration_Token() {
        refreshToken.setExpiry(Instant.now().plusSeconds(3600));

        assertDoesNotThrow(() -> {
            refreshTokenService.verifyExpiration(refreshToken);
        });

        verify(refreshTokenRepository, never()).delete(refreshToken);
    }

    @Test
    void testDelete_TokenDeletionException() {
        doThrow(new RuntimeException("Database error")).when(refreshTokenRepository).delete(refreshToken);

        RefreshTokenDeletionException exception = assertThrows(RefreshTokenDeletionException.class, () -> {
            refreshTokenService.delete(refreshToken);
        });

        assertTrue(exception.getMessage().contains("Error deleting refresh token for token: " + refreshToken.getToken()));
    }
    @Test
    void testCreateRefreshToken_ExistingTokenExpired() {
        refreshToken.setExpiry(Instant.now().minusSeconds(3600)); // Token expired
        when(userService.getByUsername("testUser")).thenReturn(user);
        when(refreshTokenRepository.getByUserId(user.getId())).thenReturn(Optional.of(refreshToken));

        RefreshToken newToken = new RefreshToken();
        newToken.setToken(UUID.randomUUID().toString());
        newToken.setUser(user);
        newToken.setExpiry(Instant.now().plusSeconds(3600));
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(newToken);

        RefreshToken createdToken = refreshTokenService.createRefreshToken("testUser");

        assertNotNull(createdToken);
        assertNotEquals(refreshToken.getToken(), createdToken.getToken());
        verify(refreshTokenRepository).delete(refreshToken);
        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }

    @Test
    void testVerifyExpiration_TokenNotExpired() {
        refreshToken.setExpiry(Instant.now().plusSeconds(3600));

        assertDoesNotThrow(() -> refreshTokenService.verifyExpiration(refreshToken));

        verify(refreshTokenRepository, never()).delete(refreshToken);
    }

    @Test
    void testVerifyExpiration_TokenExpired() {
        refreshToken.setExpiry(Instant.now().minusSeconds(3600));

        assertThrows(RefreshTokenExpirationCheckException.class, () -> refreshTokenService.verifyExpiration(refreshToken));

        verify(refreshTokenRepository).delete(refreshToken);
    }

    @Test
    void testGetByToken() {
        when(refreshTokenRepository.getByToken(refreshToken.getToken())).thenReturn(refreshToken);

        RefreshToken foundToken = refreshTokenService.getByToken(refreshToken.getToken());

        assertNotNull(foundToken);
        assertEquals(refreshToken.getToken(), foundToken.getToken());
    }

    @Test
    void testGetByToken_NotFound() {
        when(refreshTokenRepository.getByToken("invalidToken")).thenReturn(null);

        assertThrows(RefreshTokenNotFoundException.class, () -> refreshTokenService.getByToken("invalidToken"));
    }

    @Test
    void testDelete() {
        assertDoesNotThrow(() -> refreshTokenService.delete(refreshToken));

        verify(refreshTokenRepository).delete(refreshToken);
    }

    @Test
    void testDeleteByToken_RefreshTokenDeletionException() {
        String token = "testToken";
        doThrow(new DataAccessException("Database error") {}).when(refreshTokenRepository).deleteByToken(token);

        RefreshTokenDeletionException exception = assertThrows(RefreshTokenDeletionException.class, () -> {
            refreshTokenService.deleteByToken(token);
        });

        assertTrue(exception.getMessage().contains("Error deleting refresh token for token: " + token));
    }

    @Test
    void testTokenExpired_Exception() {
        RefreshToken token = mock(RefreshToken.class);

        // Simulate the exception being thrown when getExpiry() is called
        when(token.getExpiry()).thenThrow(new RuntimeException("Unexpected error"));

        RefreshTokenExpirationCheckException exception = assertThrows(RefreshTokenExpirationCheckException.class, () -> {
            refreshTokenService.tokenExpired(token);
        });

        assertTrue(exception.getMessage().contains("Error checking if refresh token has expired for token: "));
    }




    @Test
    void testDeleteByToken() {
        assertDoesNotThrow(() -> refreshTokenService.deleteByToken(refreshToken.getToken()));

        verify(refreshTokenRepository).deleteByToken(refreshToken.getToken());
    }

    @Test
    void testGetExpiryByUsername() {
        Instant expiry = Instant.now().plusSeconds(3600);
        when(refreshTokenRepository.getExpiryByUsername("testUser")).thenReturn(expiry);

        Instant result = refreshTokenService.getExpiryByUsername("testUser");

        assertNotNull(result);
        assertEquals(expiry, result);
    }

    @Test
    void testCreateNewRefreshToken_UserIsNull() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            refreshTokenService.createNewRefreshToken(null);
        });

        assertEquals("User cannot be null.", exception.getMessage());
    }

    @Test
    void testCreateNewRefreshToken_ExceptionDuringSave() {
        User user2 = new User();
        user2.setUsername("testUser");

        when(refreshTokenRepository.save(any(RefreshToken.class)))
                .thenThrow(new RuntimeException("Database error"));

        RefreshTokenCreationException exception = assertThrows(RefreshTokenCreationException.class, () -> {
            refreshTokenService.createNewRefreshToken(user2);
        });

        assertTrue(exception.getMessage().contains("Error creating refresh token for user: " + user2.getUsername()));
    }

}
