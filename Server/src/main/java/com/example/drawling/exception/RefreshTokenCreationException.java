package com.example.drawling.exception;

public class RefreshTokenCreationException extends RuntimeException {
    public RefreshTokenCreationException(String message, Throwable cause) {
        super(message, cause);
    }
}
