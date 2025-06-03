package com.example.drawling.exception;

public class RefreshTokenNotMatchUserException extends RuntimeException {
    public RefreshTokenNotMatchUserException(String message) {
        super(message);
    }
}
