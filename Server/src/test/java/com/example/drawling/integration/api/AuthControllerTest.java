package com.example.drawling.integration.api;

import com.example.drawling.application.controller.AuthController;
import com.example.drawling.business.helper.RefreshCookieHelper;
import com.example.drawling.business.interfaces.service.RefreshTokenService;
import com.example.drawling.business.interfaces.service.UserService;
import com.example.drawling.domain.dto.AuthRequest;
import com.example.drawling.domain.dto.AuthResponse;
import com.example.drawling.domain.dto.RegisterDTO;
import com.example.drawling.domain.model.CustomUserDetails;
import com.example.drawling.domain.model.RefreshToken;
import com.example.drawling.domain.model.User;
import com.example.drawling.security.CustomUserDetailsService;
import com.example.drawling.utility.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserService userService;

    @Mock
    private CustomUserDetailsService customUserDetailsService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private RefreshCookieHelper refreshCookieHelper;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        // mocks
    }




    @Test
    void testRegister() {
        RegisterDTO registerDTO = new RegisterDTO("testUser", "password");
        User user = new User();
        user.setUsername(registerDTO.getUsername());
        user.setPassword(registerDTO.getPassword());

        when(userService.save(any(User.class))).thenReturn(user);

        ResponseEntity<String> response = authController.register(registerDTO);

        assertEquals(ResponseEntity.ok("User registered successfully"), response);
        verify(userService, times(1)).save(any(User.class));
    }




    @Test
    void testRefreshTokenSuccess() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        String refreshToken = "validRefreshToken";
        String username = "testUser";
        String password = "password123";
        int userId = 1;
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        String jwtToken = "newJwtToken";

        RefreshToken mockRefreshToken = new RefreshToken();
        mockRefreshToken.setToken(refreshToken);
        User mockUser = new User();
        mockUser.setUsername(username);
        mockUser.setPassword(password);
        mockUser.setId(userId);
        mockRefreshToken.setUser(mockUser);

        CustomUserDetails mockUserDetails = new CustomUserDetails(username, password, userId, authorities);
        ResponseCookie responseCookie = ResponseCookie.from("refreshToken", refreshToken).build();

        when(refreshCookieHelper.getRefreshTokenFromRequest(request)).thenReturn(refreshToken);
        when(refreshTokenService.getByToken(refreshToken)).thenReturn(mockRefreshToken);
        doNothing().when(refreshTokenService).verifyExpiration(mockRefreshToken);
        when(customUserDetailsService.loadUserByUsername(username)).thenReturn(mockUserDetails);
        when(jwtUtil.generateToken(any(Authentication.class))).thenReturn(jwtToken);
        when(refreshCookieHelper.createRefreshCookie(eq(refreshToken), any())).thenReturn(responseCookie);

        ResponseEntity<AuthResponse> response = authController.refreshToken(request);

        assertEquals(jwtToken, response.getBody().getJwtToken());
        assertEquals(responseCookie.toString(), response.getHeaders().get(HttpHeaders.SET_COOKIE).get(0));
        verify(refreshTokenService, times(1)).verifyExpiration(mockRefreshToken);
        verify(jwtUtil, times(1)).generateToken(any(Authentication.class));
    }


    @Test
    void testLoginSuccess() {
        AuthRequest authRequest = new AuthRequest("testUser", "password123");
        String refreshTokenValue = "newRefreshToken";
        String jwtTokenValue = "newJwtToken";
        int userId = 1;

        RefreshToken mockRefreshToken = new RefreshToken();
        mockRefreshToken.setToken(refreshTokenValue);
        User mockUser = new User();
        mockUser.setUsername(authRequest.getUsername());
        mockUser.setPassword(authRequest.getPassword());
        mockUser.setId(userId);


        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(true);

        when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(authentication);
        when(refreshTokenService.createRefreshToken(authRequest.getUsername())).thenReturn(mockRefreshToken);
        when(jwtUtil.generateToken(authentication)).thenReturn(jwtTokenValue);
        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", refreshTokenValue).build();
        when(refreshCookieHelper.createRefreshCookie(eq(refreshTokenValue), any())).thenReturn(refreshCookie);

        ResponseEntity<AuthResponse> response = authController.login(authRequest);

        assertEquals(ResponseEntity.ok().body(new AuthResponse(jwtTokenValue)).getBody().getJwtToken(), response.getBody().getJwtToken());
        assertEquals(refreshCookie.toString(), response.getHeaders().get(HttpHeaders.SET_COOKIE).get(0));
        verify(authenticationManager, times(1)).authenticate(any(Authentication.class));
        verify(refreshTokenService, times(1)).createRefreshToken(authRequest.getUsername());
        verify(jwtUtil, times(1)).generateToken(authentication);
    }


    @Test
    void testLoginFailure() {
        AuthRequest authRequest = new AuthRequest("invalidUser", "wrongPassword");

        when(authenticationManager.authenticate(any(Authentication.class)))
                .thenThrow(new UsernameNotFoundException("Invalid user request!"));

        ResponseEntity<AuthResponse> response = authController.login(authRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid user request!", response.getBody().getJwtToken());
        verify(authenticationManager, times(1)).authenticate(any(Authentication.class));
        verify(refreshTokenService, never()).createRefreshToken(anyString());
        verify(jwtUtil, never()).generateToken(any(Authentication.class));
    }


    @Test
    void testLoginInternalServerError() {
        AuthRequest authRequest = new AuthRequest("testUser", "password123");

        when(authenticationManager.authenticate(any(Authentication.class)))
                .thenThrow(new RuntimeException("Unexpected error occurred"));

        ResponseEntity<AuthResponse> response = authController.login(authRequest);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
        verify(authenticationManager, times(1)).authenticate(any(Authentication.class));
        verify(refreshTokenService, never()).createRefreshToken(anyString());
        verify(jwtUtil, never()).generateToken(any(Authentication.class));
    }

    @Test
    void testLoginBadRequestDueToUnauthenticatedUser() {
        AuthRequest authRequest = new AuthRequest("testUser", "password123");

        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(false);

        ResponseEntity<AuthResponse> response = authController.login(authRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
        verify(authenticationManager, times(1)).authenticate(any(Authentication.class));
        verify(refreshTokenService, never()).createRefreshToken(anyString());
        verify(jwtUtil, never()).generateToken(any(Authentication.class));
    }



    @Test
    void testRefreshTokenExpired() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        String refreshToken = "expiredRefreshToken";

        RefreshToken mockRefreshToken = new RefreshToken();
        mockRefreshToken.setToken(refreshToken);

        when(refreshCookieHelper.getRefreshTokenFromRequest(request)).thenReturn(refreshToken);
        when(refreshTokenService.getByToken(refreshToken)).thenReturn(mockRefreshToken);
        doThrow(new RuntimeException("Refresh token expired")).when(refreshTokenService).verifyExpiration(mockRefreshToken);

        try {
            authController.refreshToken(request);
        } catch (RuntimeException e) {
            assertEquals("Refresh token expired", e.getMessage());
        }

        verify(refreshTokenService, times(1)).verifyExpiration(mockRefreshToken);
    }

    @Test
    void testRefreshTokenInvalid() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        String refreshToken = "invalidRefreshToken";

        when(refreshCookieHelper.getRefreshTokenFromRequest(request)).thenReturn(refreshToken);
        when(refreshTokenService.getByToken(refreshToken)).thenThrow(new RuntimeException("Invalid refresh token"));

        try {
            authController.refreshToken(request);
        } catch (RuntimeException e) {
            assertEquals("Invalid refresh token", e.getMessage());
        }

        verify(refreshTokenService, times(1)).getByToken(refreshToken);
    }

    @Test
    void testLogout() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        String refreshToken = "refreshToken123";

        when(refreshCookieHelper.getRefreshTokenFromRequest(request)).thenReturn(refreshToken);

        ResponseEntity<String> response = authController.logout(request);

        assertEquals(ResponseEntity.ok("Logout successful"), response);
        verify(refreshTokenService, times(1)).deleteByToken(refreshToken);
    }

    @Test
    void testLoginInvalidUser() {
        AuthRequest authRequest = new AuthRequest("invalidUser", "wrongPassword");

        when(authenticationManager.authenticate(any())).thenThrow(new UsernameNotFoundException("Invalid user request!"));

        try {
            authController.login(authRequest);
        } catch (UsernameNotFoundException e) {
            assertEquals("Invalid user request!", e.getMessage());
        }

        verify(authenticationManager, times(1)).authenticate(any());
    }
}

