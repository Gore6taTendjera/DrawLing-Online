package com.example.drawling.business.helper;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseCookie;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RefreshCookieHelperTest {

    @InjectMocks
    private RefreshCookieHelper refreshCookieHelper;

    @Mock
    private HttpServletRequest request;



    @Test
    void testGetRefreshTokenFromRequest_WithCookie() {
        Cookie[] cookies = new Cookie[]{new Cookie("refreshToken", "sampleToken")};
        when(request.getCookies()).thenReturn(cookies);

        String token = refreshCookieHelper.getRefreshTokenFromRequest(request);

        assertEquals("sampleToken", token);
    }

    @Test
    void testGetRefreshTokenFromRequest_WithoutCookie() {
        when(request.getCookies()).thenReturn(null);

        String token = refreshCookieHelper.getRefreshTokenFromRequest(request);

        assertNull(token);
    }

    @Test
    void testGetRefreshTokenFromRequest_NoMatchingCookie() {
        Cookie[] cookies = new Cookie[]{new Cookie("otherToken", "sampleToken")};
        when(request.getCookies()).thenReturn(cookies);

        String token = refreshCookieHelper.getRefreshTokenFromRequest(request);

        assertNull(token);
    }



    @Test
    void testCreateRefreshCookie() {
        // Arrange
        String token = "sampleToken";
        Instant expiry = Instant.now().plusSeconds(3600); // 1 hour from now
        int expectedMaxAge = (int) expiry.getEpochSecond() - (int) Instant.now().getEpochSecond();

        // Act
        ResponseCookie result = refreshCookieHelper.createRefreshCookie(token, expiry);

        // Assert
        Assertions.assertNotNull(result, "ResponseCookie should not be null");
        Assertions.assertEquals("refreshToken", result.getName(), "Cookie name should be 'refreshToken'");
        Assertions.assertEquals(token, result.getValue(), "Cookie value should match the token");
        Assertions.assertTrue(result.isHttpOnly(), "Cookie should be HTTP-only");
        Assertions.assertTrue(result.isSecure(), "Cookie should be secure");
        Assertions.assertEquals("/", result.getPath(), "Cookie path should be '/'");
        Assertions.assertEquals(expectedMaxAge, result.getMaxAge().getSeconds(), "Cookie max age should match the expected value");
        Assertions.assertEquals("Strict", result.getSameSite(), "Cookie SameSite attribute should be 'Strict'");
        Assertions.assertEquals("localhost", result.getDomain(), "Cookie domain should be 'localhost'");
    }

}