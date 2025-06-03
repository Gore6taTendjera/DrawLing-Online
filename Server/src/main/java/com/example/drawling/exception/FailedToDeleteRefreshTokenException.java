package com.example.drawling.exception;

public class FailedToDeleteRefreshTokenException extends RuntimeException {
    public FailedToDeleteRefreshTokenException(String s, Exception e) {
        super(s, e);
    }
}
