package com.hotel.lostfound.location;

public class UnknownZoneException extends RuntimeException {

    public UnknownZoneException(double x, double y) {
        super("No hotel zone contains point (" + x + ", " + y + ")");
    }
}
