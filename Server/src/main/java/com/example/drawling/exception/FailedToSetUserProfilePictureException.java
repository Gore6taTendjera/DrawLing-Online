package com.example.drawling.exception;

public class FailedToSetUserProfilePictureException extends RuntimeException {
    public FailedToSetUserProfilePictureException(String message) {
        super(message);
    }
    public FailedToSetUserProfilePictureException(String message, Throwable cause) {
        super(message, cause);
    }
}
