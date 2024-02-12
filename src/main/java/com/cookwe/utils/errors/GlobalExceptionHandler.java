package com.cookwe.utils.errors;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<String> handleThrowable(Throwable exception) {
        System.out.println(exception.getMessage());

        if (exception instanceof ErrorCode) {
            ErrorCode errorCode = (ErrorCode) exception;
            return ResponseEntity.status(errorCode.getCode()).body(exception.getMessage());
        }

        if (exception instanceof AccessDeniedException) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(exception.getMessage());
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error");
    }
}
