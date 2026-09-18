package com.hotel.lostfound.inventory;

import java.time.Year;

public interface TrackingCodeGenerator {

    default String next() {
        return next(Year.now().getValue());
    }

    String next(int year);
}
