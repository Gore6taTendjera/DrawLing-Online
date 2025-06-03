package com.example.drawling.exception;

public class FailedToSaveRefreshTokenException extends RuntimeException {
    public FailedToSaveRefreshTokenException(String s, Exception e) {
        super(s, e);
    }
}
