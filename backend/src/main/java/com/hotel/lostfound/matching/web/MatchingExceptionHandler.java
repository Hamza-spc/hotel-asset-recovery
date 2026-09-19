package com.hotel.lostfound.matching.web;

import com.hotel.lostfound.matching.MatchException;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
class MatchingExceptionHandler {

    @ExceptionHandler(MatchException.class)
    ResponseEntity<Map<String, String>> failed(MatchException ex) {
        HttpStatus status = ex.getMessage() != null && ex.getMessage().contains("not found")
                ? HttpStatus.NOT_FOUND
                : ex.getMessage() != null && ex.getMessage().contains("Ollama")
                        ? HttpStatus.SERVICE_UNAVAILABLE
                        : HttpStatus.CONFLICT;
        return ResponseEntity.status(status).body(Map.of("message", ex.getMessage()));
    }
}
