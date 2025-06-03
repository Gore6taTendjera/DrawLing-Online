package com.example.drawling.exception;

public class RefreshTokenExpirationCheckException extends RuntimeException {
    public RefreshTokenExpirationCheckException(String message, Throwable cause) {
        super(message, cause);
    }
}
