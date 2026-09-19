package com.hotel.lostfound.claims;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class LossReport {

    private final UUID id;
    private final String guestName;
    private final String roomNumber;
    private final String contact;
    private final String description;
    private String photoObjectKey;
    private final String zoneName;
    private final Double mapX;
    private final Double mapY;
    private LossReportStatus status;
    private final String filedBy;
    private final Instant filedAt;
    private final long version;
    private final List<Object> events = new ArrayList<>();

    private LossReport(
            UUID id,
            String guestName,
            String roomNumber,
            String contact,
            String description,
            String photoObjectKey,
            String zoneName,
            Double mapX,
            Double mapY,
            LossReportStatus status,
            String filedBy,
            Instant filedAt,
            long version) {
        this.id = id;
        this.guestName = guestName;
        this.roomNumber = roomNumber;
        this.contact = contact;
        this.description = description;
        this.photoObjectKey = photoObjectKey;
        this.zoneName = zoneName;
        this.mapX = mapX;
        this.mapY = mapY;
        this.status = status;
        this.filedBy = filedBy;
        this.filedAt = filedAt;
        this.version = version;
    }

    public static LossReport file(
            String guestName,
            String roomNumber,
            String contact,
            String description,
            String zoneName,
            MapLocation point,
            String photoObjectKey,
            String filedBy,
            Instant filedAt) {
        LossReport report = new LossReport(
                UUID.randomUUID(),
                require(guestName, "guestName"),
                require(roomNumber, "roomNumber"),
                require(contact, "contact"),
                require(description, "description"),
                photoObjectKey,
                require(zoneName, "zoneName"),
                point == null ? null : point.x(),
                point == null ? null : point.y(),
                LossReportStatus.OPEN,
                require(filedBy, "filedBy"),
                filedAt,
                0);
        report.events.add(new LossReportFiled(
                report.id, report.guestName, report.description, report.zoneName, report.mapX, report.mapY, filedAt));
        return report;
    }

    public static LossReport rehydrate(
            UUID id,
            String guestName,
            String roomNumber,
            String contact,
            String description,
            String photoObjectKey,
            String zoneName,
            Double mapX,
            Double mapY,
            LossReportStatus status,
            String filedBy,
            Instant filedAt,
            long version) {
        return new LossReport(
                id,
                guestName,
                roomNumber,
                contact,
                description,
                photoObjectKey,
                zoneName,
                mapX,
                mapY,
                status,
                filedBy,
                filedAt,
                version);
    }

    public void attachPhoto(String objectKey) {
        this.photoObjectKey = require(objectKey, "photo");
    }

    public void close() {
        if (status == LossReportStatus.RESOLVED || status == LossReportStatus.CLOSED) {
            throw new ClaimException("Loss report is already " + status);
        }
        this.status = LossReportStatus.CLOSED;
        events.add(new LossReportClosed(id, Instant.now()));
    }

    public void markMatched() {
        if (status != LossReportStatus.OPEN) {
            throw new ClaimException("Only open reports can be matched");
        }
        this.status = LossReportStatus.MATCHED;
    }

    public void reopen() {
        if (status != LossReportStatus.MATCHED) {
            throw new ClaimException("Only matched reports can be reopened");
        }
        this.status = LossReportStatus.OPEN;
    }

    public void resolve() {
        if (status == LossReportStatus.CLOSED) {
            throw new ClaimException("Closed reports cannot be resolved");
        }
        this.status = LossReportStatus.RESOLVED;
    }

    public List<Object> pullEvents() {
        List<Object> copy = List.copyOf(events);
        events.clear();
        return copy;
    }

    public UUID id() {
        return id;
    }

    public String guestName() {
        return guestName;
    }

    public String roomNumber() {
        return roomNumber;
    }

    public String contact() {
        return contact;
    }

    public String description() {
        return description;
    }

    public Optional<String> photoObjectKey() {
        return Optional.ofNullable(photoObjectKey);
    }

    public String zoneName() {
        return zoneName;
    }

    public Optional<MapLocation> mapPoint() {
        if (mapX == null || mapY == null) {
            return Optional.empty();
        }
        return Optional.of(new MapLocation(mapX, mapY));
    }

    public LossReportStatus status() {
        return status;
    }

    public String filedBy() {
        return filedBy;
    }

    public Instant filedAt() {
        return filedAt;
    }

    public long version() {
        return version;
    }

    private static String require(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " is required");
        }
        return value.trim();
    }
}
