package com.hotel.lostfound.claims.web;

import com.hotel.lostfound.claims.LossReport;
import com.hotel.lostfound.claims.LossReportStatus;
import java.time.Instant;
import java.util.UUID;

record GuestReportResponse(
        UUID id,
        String guestName,
        String roomNumber,
        String description,
        String zoneName,
        LossReportStatus status,
        Instant filedAt) {

    static GuestReportResponse from(LossReport report) {
        return new GuestReportResponse(
                report.id(),
                report.guestName(),
                report.roomNumber(),
                report.description(),
                report.zoneName(),
                report.status(),
                report.filedAt());
    }
}
