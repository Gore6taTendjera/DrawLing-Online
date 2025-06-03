package com.example.drawling.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfiguration implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:4173", "http://localhost:5173", "http://145.93.76.172:5173")
                .allowedHeaders("Authorization", "Origin", "Content-Type", "Accept", "X-XSRF-TOKEN", "XSRF-TOKEN")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD", "PATCH")
                .allowCredentials(true);
    }
}
