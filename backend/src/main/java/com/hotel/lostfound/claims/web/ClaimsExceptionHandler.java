package com.hotel.lostfound.claims.web;

import com.hotel.lostfound.claims.ClaimException;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
class ClaimsExceptionHandler {

    @ExceptionHandler(ClaimException.class)
    ResponseEntity<Map<String, String>> claim(ClaimException ex) {
        HttpStatus status = ex.getMessage() != null && ex.getMessage().contains("not found")
                ? HttpStatus.NOT_FOUND
                : HttpStatus.CONFLICT;
        return ResponseEntity.status(status).body(Map.of("message", ex.getMessage()));
    }
}
