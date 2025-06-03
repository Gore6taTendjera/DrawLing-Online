package com.example.drawling.application.handler.exception;


import com.example.drawling.exception.ExperienceLevelUpdateException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExperienceLevelControllerExceptionHandler {

    @ExceptionHandler(ExperienceLevelUpdateException.class)
    public ResponseEntity<String> handleExperienceLevelUpdateException(ExperienceLevelUpdateException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ex.getMessage());
    }
}
