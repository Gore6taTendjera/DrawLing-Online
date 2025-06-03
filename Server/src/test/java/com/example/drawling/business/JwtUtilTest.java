package com.example.drawling.business;

import com.example.drawling.domain.model.CustomUserDetails;
import com.example.drawling.utility.JwtUtil;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import javax.crypto.SecretKey;
import java.lang.reflect.Field;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtUtilTest {

    private JwtUtil jwtUtil;

    @Mock
    private Authentication authentication;

    @BeforeEach
    void setUp() throws Exception {
        jwtUtil = new JwtUtil();

        // Set private fields using reflection
        Field secretField = JwtUtil.class.getDeclaredField("secret");
        secretField.setAccessible(true);
        secretField.set(jwtUtil, "testSecretBase64EncodedKey123456789012345678901234");

        Field expirationField = JwtUtil.class.getDeclaredField("expiration");
        expirationField.setAccessible(true);
        expirationField.set(jwtUtil, 60000L); // 1 minute
    }

    @Test
    void testGenerateToken() {
        // Arrange
        CustomUserDetails userDetails = mock(CustomUserDetails.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("testUser");
        when(userDetails.getUserId()).thenReturn(123);

        // Act
        String token = jwtUtil.generateToken(authentication);

        // Assert
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void testValidateToken_Valid() {
        // Arrange
        CustomUserDetails userDetails = mock(CustomUserDetails.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("testUser");
        when(userDetails.getUserId()).thenReturn(123);

        String token = jwtUtil.generateToken(authentication);

        // Act
        boolean isValid = jwtUtil.validateToken(token, "testUser");

        // Assert
        assertTrue(isValid);
    }

    @Test
    void testValidateToken_InvalidUsername() {
        // Arrange
        CustomUserDetails userDetails = mock(CustomUserDetails.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("testUser");
        when(userDetails.getUserId()).thenReturn(123);

        String token = jwtUtil.generateToken(authentication);

        // Act
        boolean isValid = jwtUtil.validateToken(token, "wrongUser");

        // Assert
        assertFalse(isValid);
    }

    @Test
    void testExtractUsername() {
        // Arrange
        CustomUserDetails userDetails = mock(CustomUserDetails.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("testUser");
        when(userDetails.getUserId()).thenReturn(123);

        String token = jwtUtil.generateToken(authentication);

        // Act
        String username = jwtUtil.extractUsername(token);

        // Assert
        assertEquals("testUser", username);
    }

    @Test
    void testIsTokenExpired_NotExpired() {
        // Arrange
        CustomUserDetails userDetails = mock(CustomUserDetails.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("testUser");
        when(userDetails.getUserId()).thenReturn(123);

        String token = jwtUtil.generateToken(authentication);

        // Act
        boolean isExpired = jwtUtil.isTokenExpired(token);

        // Assert
        assertFalse(isExpired);
    }


    @Test
    void testIsTokenExpired_Expired_WithMocking() throws NoSuchFieldException, IllegalAccessException {
        // Arrange
        Field expirationField = JwtUtil.class.getDeclaredField("expiration");
        expirationField.setAccessible(true);
        expirationField.set(jwtUtil, 5000L);

        String expiredToken = "expiredTokenString";

        Claims claims = mock(Claims.class);
        when(claims.getExpiration()).thenReturn(new Date(System.currentTimeMillis() - 1000));

        JwtUtil spyJwtUtil = spy(jwtUtil);
        doReturn(claims).when(spyJwtUtil).extractAllClaims(expiredToken);

        // Act
        boolean isExpired = spyJwtUtil.isTokenExpired(expiredToken);

        // Assert
        assertTrue(isExpired, "The token should be expired.");
    }





    @Test
    void testExtractAllClaims() {
        // Arrange
        CustomUserDetails userDetails = mock(CustomUserDetails.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("testUser");
        when(userDetails.getUserId()).thenReturn(123);

        String token = jwtUtil.generateToken(authentication);

        // Act
        Claims claims = jwtUtil.extractAllClaims(token);

        // Assert
        assertEquals("testUser", claims.getSubject());
        assertEquals(123, claims.get("userId"));
    }

    @Test
    void testGetSignKey() {
        // Act
        SecretKey signKey = jwtUtil.getSignKey();

        // Assert
        assertNotNull(signKey);
    }
}
