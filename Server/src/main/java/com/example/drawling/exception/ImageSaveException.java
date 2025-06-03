package com.example.drawling.exception;

public class ImageSaveException extends RuntimeException {
    public ImageSaveException(String s, Exception e) {
        super(s, e);
    }
}
