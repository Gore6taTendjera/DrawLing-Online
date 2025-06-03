package com.example.drawling.business.helper;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class RefreshCookieHelper {
    @Value("${refreshToken.expiration}")
    private int cookieMaxAge;

    public RefreshCookieHelper() {
        //
    }


    public ResponseCookie createRefreshCookie(String token, Instant expiry){
        return ResponseCookie.from("refreshToken", token)
                .httpOnly(true)
                .path("/")
                .maxAge((int) expiry.getEpochSecond() - Instant.now().getEpochSecond())
                .build();
    }


    public String getRefreshTokenFromRequest(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refreshToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

}
