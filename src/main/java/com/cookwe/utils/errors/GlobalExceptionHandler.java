package com.cookwe.utils.errors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Throwable.class)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> handleThrowable(Throwable exception, WebRequest request) {
        HttpStatus status;
        String message;

        if (exception instanceof ErrorCode errorCode) {
            status = HttpStatus.valueOf(errorCode.getCode());
            message = errorCode.getMessage();
        } else if (exception instanceof AccessDeniedException) {
            status = HttpStatus.FORBIDDEN;
            message = exception.getMessage();
        } else if (exception instanceof AuthenticationException) {
            status = HttpStatus.BAD_REQUEST;
            message = exception.getMessage();
        } else if (exception instanceof MethodArgumentNotValidException) {
            FieldError error = ((MethodArgumentNotValidException) exception).getBindingResult().getFieldError();
            status = HttpStatus.BAD_REQUEST;
            message = error != null ? error.getDefaultMessage() : "Invalid input";
        } else if (exception instanceof HttpMessageConversionException) {
            status = HttpStatus.BAD_REQUEST;
            message = "Invalid input";
        } else {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            message = "Internal Server Error";
        }

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("status", status.value());
        responseBody.put("message", message);

        return new ResponseEntity<>(responseBody, status);
    }
}