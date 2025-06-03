package com.example.drawling.application.handler.exception;

import com.example.drawling.application.controller.BalanceController;
import com.example.drawling.exception.BalanceUpdateException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice(assignableTypes = BalanceController.class)
public class BalanceControllerExceptionHandler {

    @ExceptionHandler(BalanceUpdateException.class)
    public ResponseEntity<String> handleBalanceUpdateException(BalanceUpdateException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ex.getMessage());
    }


}
