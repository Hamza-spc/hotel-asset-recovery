package com.hotel.lostfound.audit.web;

import com.hotel.lostfound.audit.AuditEntry;
import java.time.Instant;
import java.util.UUID;

record AuditEntryResponse(
        UUID id,
        String eventType,
        String aggregateType,
        UUID aggregateId,
        String summary,
        Instant occurredAt) {

    static AuditEntryResponse from(AuditEntry entry) {
        return new AuditEntryResponse(
                entry.id(),
                entry.eventType(),
                entry.aggregateType(),
                entry.aggregateId(),
                entry.summary(),
                entry.occurredAt());
    }
}
