package com.example.drawling.business.config;

import com.example.drawling.filter.JwtFilter;
import com.example.drawling.security.SecurityConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class SecurityConfigurationTest {

    @InjectMocks
    private SecurityConfiguration securityConfiguration;

    @Mock
    private JwtFilter jwtFilter;

    @Mock
    private UserDetailsService userDetailsService;

    @BeforeEach
    void setup() {
        // mocks
    }

    @Test
    void testAuthenticationManagerBean() {
        AuthenticationManager authenticationManager = securityConfiguration.authenticationManager(userDetailsService);

        assertNotNull(authenticationManager);
        assertInstanceOf(ProviderManager.class, authenticationManager);
    }

    @Test
    void testPasswordEncoderBean() {
        PasswordEncoder passwordEncoder = securityConfiguration.passwordEncoder();

        assertNotNull(passwordEncoder);
        assertInstanceOf(BCryptPasswordEncoder.class, passwordEncoder);
    }


}