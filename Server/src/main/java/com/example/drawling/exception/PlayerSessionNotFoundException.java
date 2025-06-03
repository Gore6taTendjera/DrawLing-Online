package com.example.drawling.exception;

public class PlayerSessionNotFoundException extends RuntimeException {
    public PlayerSessionNotFoundException(String message) {
        super(message);
    }
}
