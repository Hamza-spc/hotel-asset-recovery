package com.hotel.lostfound.claims;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import org.junit.jupiter.api.Test;

class LossReportTests {

    @Test
    void filedReportKeepsTapToPinLocation() {
        LossReport report = LossReport.file(
                "Ada Guest",
                "412",
                "ada@example.com",
                "Black leather wallet",
                "Lobby",
                new MapLocation(20, 55),
                null,
                "frontdesk",
                Instant.parse("2026-09-19T00:00:00Z"));

        assertEquals("Lobby", report.zoneName());
        assertTrue(report.mapPoint().isPresent());
        assertEquals(20, report.mapPoint().orElseThrow().x());
        assertEquals(55, report.mapPoint().orElseThrow().y());
        assertTrue(report.pullEvents().stream().anyMatch(LossReportFiled.class::isInstance));
    }
}
