package com.example.drawling.business.config;


import com.example.drawling.security.CorsConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.servlet.config.annotation.CorsRegistration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CorsConfigurationTest {

    private CorsRegistry corsRegistry;
    private CorsRegistration corsRegistration;

    @BeforeEach
    public void setUp() {
        corsRegistry = Mockito.mock(CorsRegistry.class);
        corsRegistration = Mockito.mock(CorsRegistration.class);

        when(corsRegistry.addMapping("/**")).thenReturn(corsRegistration);

        when(corsRegistration.allowedOrigins("http://localhost:4173", "http://localhost:5173"))
                .thenReturn(corsRegistration);
        when(corsRegistration.allowedMethods("*")).thenReturn(corsRegistration);
        when(corsRegistration.allowedHeaders("*")).thenReturn(corsRegistration);
        when(corsRegistration.allowCredentials(true)).thenReturn(corsRegistration);
    }

    @Test
    void testAddCorsMappings() {
        // Arrange
        CorsConfiguration corsConfiguration2 = new CorsConfiguration();
        CorsRegistry registry = Mockito.mock(CorsRegistry.class);
        CorsRegistration corsRegistration2 = Mockito.mock(CorsRegistration.class);

        // Mock the behavior of the CorsRegistry
        when(registry.addMapping("/**")).thenReturn(corsRegistration2);
        when(corsRegistration2.allowedOrigins("http://localhost:4173", "http://localhost:5173")).thenReturn(corsRegistration2);
        when(corsRegistration2.allowedHeaders(any(String[].class))).thenReturn(corsRegistration2);
        when(corsRegistration2.allowedMethods(any(String[].class))).thenReturn(corsRegistration2);
        when(corsRegistration2.allowCredentials(true)).thenReturn(corsRegistration2);

        // Act
        corsConfiguration2.addCorsMappings(registry);

        // Assert
        verify(registry).addMapping("/**");
        verify(corsRegistration2).allowedOrigins("http://localhost:4173", "http://localhost:5173");
        verify(corsRegistration2).allowedHeaders("Authorization", "Origin", "Content-Type", "Accept", "X-XSRF-TOKEN", "XSRF-TOKEN");
        verify(corsRegistration2).allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD", "PATCH");
        verify(corsRegistration2).allowCredentials(true);
    }
}