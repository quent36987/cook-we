package com.cookwe.utils.errors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<String> handleThrowable(Throwable exception) {
        if (exception instanceof ErrorCode errorCode) {
            return ResponseEntity.status(errorCode.getCode()).body(exception.getMessage());
        }

        if (exception instanceof AccessDeniedException) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(exception.getMessage());
        }

        if (exception instanceof AuthenticationException) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
        }

        if (exception instanceof MethodArgumentNotValidException) {
            FieldError error = ((MethodArgumentNotValidException) exception).getBindingResult().getFieldError();
            String message = error != null ? error.getDefaultMessage() : "Invalid input";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
        }

        System.out.println(exception.toString());
        System.out.println(exception.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error");
    }
}
