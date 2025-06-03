package com.example.drawling.filter;

import com.example.drawling.domain.enums.Role;
import com.example.drawling.security.CustomUserDetailsService;
import com.example.drawling.utility.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class JwtFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService customUserDetailsService;

    private static final List<String> ALLOWED_ORIGINS = Arrays.asList(
            "http://localhost:5173",
            "http://localhost:4173",
            "http://145.93.76.172:5173"
    );

    public JwtFilter(JwtUtil jwtUtil, CustomUserDetailsService customUserDetailsService) {
        this.jwtUtil = jwtUtil;
        this.customUserDetailsService = customUserDetailsService;
    }

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        try {
            // Handle CORS preflight requests
            if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
                handleCorsPreflight(request, response);
                return;
            }

            // Set CORS headers for actual requests
            setCorsHeaders(request, response);

            // Extract token and validate
            String jwtToken = extractToken(request);
            if (jwtToken != null) {
                processAuthentication(jwtToken, response);
            }

            // Proceed with the filter chain
            chain.doFilter(request, response);
        } catch (Exception e) {
            log.error("Error in JwtFilter: {}", e.getMessage(), e);
            if (!response.isCommitted()) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "An unexpected error occurred");
            }
        }
    }


    private String extractToken(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }
        return null;
    }

    private void processAuthentication(String jwtToken, HttpServletResponse response) throws IOException {
        try {
            String username = jwtUtil.extractUsername(jwtToken);
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

                if (jwtUtil.validateToken(jwtToken, username)) {
                    // Extract roles from JWT claims
                    Claims claims = jwtUtil.extractAllClaims(jwtToken);
                    List<String> roles = claims.get("role", List.class);

                    // Add roles and permissions to GrantedAuthority
                    Collection<GrantedAuthority> authorities = roles.stream()
                            .flatMap(role -> {
                                // Convert role to authorities (permissions + role itself)
                                Role userRole = Role.valueOf(role); // Match enum
                                return userRole.getAuthorities().stream();
                            })
                            .collect(Collectors.toList());

                    // Create authentication token
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);

                    log.info("Successfully authenticated user: {}", username);
                } else {
                    log.warn("Invalid JWT token for user: {}", username);
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT token");
                }
            }
        } catch (ExpiredJwtException e) {
            log.warn("JWT token has expired: {}", e.getMessage());
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "JWT token has expired");
        } catch (Exception e) {
            log.error("Error during JWT processing: {}", e.getMessage(), e);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authentication failed");
        }
    }




    private void handleCorsPreflight(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String origin = request.getHeader("Origin");
        if (ALLOWED_ORIGINS.contains(origin)) {
            response.setHeader("Access-Control-Allow-Origin", origin);
            response.setHeader("Access-Control-Allow-Credentials", "true"); // Allow credentials
            response.setHeader("Access-Control-Allow-Headers", "Authorization, Origin, Content-Type, Accept, X-XSRF-TOKEN, XSRF-TOKEN");
            response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD, PATCH");
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "CORS origin not allowed");
        }
    }

    private void setCorsHeaders(HttpServletRequest request, HttpServletResponse response) {
        String origin = request.getHeader("Origin");
        if (ALLOWED_ORIGINS.contains(origin)) {
            response.setHeader("Access-Control-Allow-Origin", origin);
        }
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Headers", "Authorization, Origin, Content-Type, Accept");
    }

}
