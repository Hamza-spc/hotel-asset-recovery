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
                LossReportStatus.OPEN,
                require(filedBy, "filedBy"),
                filedAt,
                0);
        report.events.add(new LossReportFiled(report.id, report.guestName, filedAt));
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
