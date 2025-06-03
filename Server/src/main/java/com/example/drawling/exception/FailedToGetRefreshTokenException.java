package com.example.drawling.exception;

public class FailedToGetRefreshTokenException extends RuntimeException {
    public FailedToGetRefreshTokenException(String s, Exception e) {
        super(s, e);
    }
}
