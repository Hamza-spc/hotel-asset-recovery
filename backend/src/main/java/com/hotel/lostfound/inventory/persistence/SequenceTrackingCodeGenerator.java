package com.hotel.lostfound.inventory.persistence;

import com.hotel.lostfound.inventory.TrackingCodeGenerator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
class SequenceTrackingCodeGenerator implements TrackingCodeGenerator {

    private final JdbcTemplate jdbc;

    SequenceTrackingCodeGenerator(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public String next(int year) {
        Long value = jdbc.queryForObject("SELECT nextval('found_item_tracking_seq')", Long.class);
        return "LF-%d-%04d".formatted(year, value == null ? 1 : value);
    }
}
