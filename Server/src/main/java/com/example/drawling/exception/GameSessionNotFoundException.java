package com.example.drawling.exception;

public class GameSessionNotFoundException extends RuntimeException {
    public GameSessionNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
