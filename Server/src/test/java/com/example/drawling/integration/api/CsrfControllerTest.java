package com.example.drawling.integration.api;

import com.example.drawling.application.controller.CsrfController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.web.csrf.CsrfToken;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CsrfControllerTest {

    @InjectMocks
    private CsrfController csrfController;

    @Mock
    private CsrfToken csrfToken;

    private MockHttpServletRequest mockRequest;
    private MockHttpServletResponse mockResponse;

    @BeforeEach
    void setUp() {
        mockRequest = new MockHttpServletRequest();
        mockResponse = new MockHttpServletResponse();
    }

    @Test
    void testGetCsrfToken() {
        // Arrange
        String mockTokenValue = "mock-token";
        when(csrfToken.getToken()).thenReturn(mockTokenValue);
        mockRequest.setAttribute(CsrfToken.class.getName(), csrfToken);

        // Act
        CsrfToken returnedToken = csrfController.getCsrfToken(mockRequest, mockResponse);

        // Assert
        assertNotNull(returnedToken, "Returned CSRF token should not be null.");
        assertEquals(mockTokenValue, returnedToken.getToken(), "Returned token value should match the mock token value.");

        // Verify the Set-Cookie header
        String setCookieHeader = mockResponse.getHeader("Set-Cookie");
        assertNotNull(setCookieHeader, "Set-Cookie header should not be null.");
        assertTrue(setCookieHeader.contains("XSRF-TOKEN=" + mockTokenValue), "Set-Cookie header should contain the correct token value.");
        assertFalse(setCookieHeader.contains("HttpOnly=false"), "Set-Cookie header should have HttpOnly=false.");
        assertTrue(setCookieHeader.contains("Secure"), "Set-Cookie header should be secure.");
        assertTrue(setCookieHeader.contains("SameSite=Strict"), "Set-Cookie header should have SameSite=Strict.");
    }
}
