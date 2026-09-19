package com.hotel.lostfound.matching;

public class MatchException extends RuntimeException {

    public MatchException(String message) {
        super(message);
    }

    public MatchException(String message, Throwable cause) {
        super(message, cause);
    }
}
