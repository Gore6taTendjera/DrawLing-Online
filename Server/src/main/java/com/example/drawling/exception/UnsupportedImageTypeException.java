package com.example.drawling.exception;

public class UnsupportedImageTypeException extends ImageValidationException {
    public UnsupportedImageTypeException(String message) {
        super(message);
    }
}