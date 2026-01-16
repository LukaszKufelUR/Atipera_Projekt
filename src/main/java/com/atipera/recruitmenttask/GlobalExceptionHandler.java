package com.atipera.recruitmenttask;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(HttpClientErrorException.NotFound.class)
    ResponseEntity<ErrorResponse> handleNotFound(HttpClientErrorException.NotFound ignored) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(404, "User not found"));
    }

    public record ErrorResponse(int status, String message) {}
}