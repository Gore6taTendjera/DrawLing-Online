package com.example.drawling.exception;

public class UserSaveFailedException extends RuntimeException {
    public UserSaveFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
