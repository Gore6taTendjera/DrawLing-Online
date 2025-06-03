package com.example.drawling.application.controller;

import com.example.drawling.business.helper.RefreshCookieHelper;
import com.example.drawling.business.interfaces.service.RefreshTokenService;
import com.example.drawling.business.interfaces.service.UserService;
import com.example.drawling.domain.dto.AuthRequest;
import com.example.drawling.domain.dto.AuthResponse;
import com.example.drawling.domain.dto.RegisterDTO;
import com.example.drawling.domain.enums.Role;
import com.example.drawling.domain.model.RefreshToken;
import com.example.drawling.domain.model.User;
import com.example.drawling.security.CustomUserDetailsService;
import com.example.drawling.utility.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
//@CrossOrigin
@RequestMapping("/api/authentication")
public class AuthController {

    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final CustomUserDetailsService customUserDetailsService;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;
    private final RefreshCookieHelper refreshCookieHelper;


    public AuthController(JwtUtil jwtUtil, UserService userService, CustomUserDetailsService customUserDetailsService, AuthenticationManager authenticationManager, RefreshTokenService refreshTokenService, RefreshCookieHelper refreshCookieHelper) {
        this.jwtUtil = jwtUtil;
        this.userService = userService;
        this.customUserDetailsService = customUserDetailsService;
        this.authenticationManager = authenticationManager;
        this.refreshTokenService = refreshTokenService;
        this.refreshCookieHelper = refreshCookieHelper;
    }


    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest authRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
            );

            if (authentication.isAuthenticated()) {
                RefreshToken refreshToken = refreshTokenService.createRefreshToken(authRequest.getUsername());
                String jwtToken = jwtUtil.generateToken(authentication);

                AuthResponse authResponse = new AuthResponse();
                authResponse.setJwtToken(jwtToken);
                SecurityContextHolder.getContext().setAuthentication(authentication);

                return ResponseEntity.ok()
                        .header(HttpHeaders.SET_COOKIE, refreshCookieHelper
                                .createRefreshCookie(refreshToken.getToken(), refreshTokenService.getExpiryByUsername(authRequest.getUsername()))
                                .toString())
                        .body(authResponse);
            }
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.badRequest().body(new AuthResponse("Invalid user request!"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        return ResponseEntity.badRequest().build();
    }



    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterDTO registerDTO) {
        User user = new User();
        user.setUsername(registerDTO.getUsername());
        user.setPassword(registerDTO.getPassword());
        user.setRole(Role.USER);

        userService.save(user);
        return ResponseEntity.ok("User registered successfully");
    }


    @PostMapping("/refresh-token")
    public ResponseEntity<AuthResponse> refreshToken(HttpServletRequest request) {

        String requestRefreshToken = refreshCookieHelper.getRefreshTokenFromRequest(request);

        RefreshToken refreshToken = refreshTokenService.getByToken(requestRefreshToken);
        refreshTokenService.verifyExpiration(refreshToken);

        String username = refreshToken.getUser().getUsername();
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwtToken = jwtUtil.generateToken(authentication);

        AuthResponse authResponse = new AuthResponse();
        authResponse.setJwtToken(jwtToken);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookieHelper.createRefreshCookie(refreshToken.getToken(), refreshTokenService.getExpiryByUsername(username)).toString())
                .body(authResponse);
    }


    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        String refreshToken = refreshCookieHelper.getRefreshTokenFromRequest(request);
        refreshTokenService.deleteByToken(refreshToken);
        return ResponseEntity.ok("Logout successful");
    }

}
