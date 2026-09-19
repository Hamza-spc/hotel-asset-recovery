package com.hotel.lostfound.location.web;

import com.hotel.lostfound.location.UnknownZoneException;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
class LocationExceptionHandler {

    @ExceptionHandler(UnknownZoneException.class)
    ResponseEntity<Map<String, String>> unknown(UnknownZoneException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", ex.getMessage()));
    }
}
