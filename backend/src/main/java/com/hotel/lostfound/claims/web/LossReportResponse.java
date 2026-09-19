package com.hotel.lostfound.claims.web;

import com.hotel.lostfound.claims.LossReport;
import com.hotel.lostfound.claims.LossReportStatus;
import java.time.Instant;
import java.util.UUID;

record LossReportResponse(
        UUID id,
        String guestName,
        String roomNumber,
        String contact,
        String description,
        String zoneName,
        Double mapX,
        Double mapY,
        LossReportStatus status,
        String filedBy,
        Instant filedAt) {

    static LossReportResponse from(LossReport report) {
        return new LossReportResponse(
                report.id(),
                report.guestName(),
                report.roomNumber(),
                report.contact(),
                report.description(),
                report.zoneName(),
                report.mapPoint().map(com.hotel.lostfound.claims.MapLocation::x).orElse(null),
                report.mapPoint().map(com.hotel.lostfound.claims.MapLocation::y).orElse(null),
                report.status(),
                report.filedBy(),
                report.filedAt());
    }
}
