package com.example.drawling.exception;

public class InsufficientPlayersException extends RuntimeException {
    public InsufficientPlayersException(String message) {
        super(message);
    }
}
