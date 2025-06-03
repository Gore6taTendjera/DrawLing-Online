package com.example.drawling.exception;

public class ImageSizeExceededException extends ImageValidationException {
    public ImageSizeExceededException(String message) {
        super(message);
    }
}