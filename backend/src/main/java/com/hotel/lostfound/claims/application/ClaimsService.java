package com.hotel.lostfound.claims.application;

import com.hotel.lostfound.claims.ClaimException;
import com.hotel.lostfound.claims.LossReport;
import com.hotel.lostfound.claims.LossReportRepository;
import com.hotel.lostfound.claims.MapLocation;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ClaimsService {

    private final LossReportRepository reports;
    private final ApplicationEventPublisher events;
    private final Clock clock;

    ClaimsService(LossReportRepository reports, ApplicationEventPublisher events, Clock clock) {
        this.reports = reports;
        this.events = events;
        this.clock = clock;
    }

    @Transactional
    public LossReport file(
            String guestName,
            String roomNumber,
            String contact,
            String description,
            String zoneName,
            MapLocation point,
            String filedBy) {
        LossReport report = LossReport.file(
                guestName, roomNumber, contact, description, zoneName, point, null, filedBy, Instant.now(clock));
        return publishAndSave(report);
    }

    @Transactional
    public LossReport close(UUID id) {
        LossReport report = require(id);
        report.close();
        return publishAndSave(report);
    }

    @Transactional(readOnly = true)
    public List<LossReport> list() {
        return reports.findAll();
    }

    @Transactional(readOnly = true)
    public LossReport get(UUID id) {
        return require(id);
    }

    private LossReport publishAndSave(LossReport report) {
        var pending = report.pullEvents();
        LossReport saved = reports.save(report);
        pending.forEach(events::publishEvent);
        return saved;
    }

    private LossReport require(UUID id) {
        return reports.findById(id).orElseThrow(() -> new ClaimException("Loss report not found: " + id));
    }
}
