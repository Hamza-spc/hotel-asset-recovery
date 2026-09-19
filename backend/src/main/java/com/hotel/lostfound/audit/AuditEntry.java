package com.hotel.lostfound.audit;

import java.time.Instant;
import java.util.UUID;

public record AuditEntry(
        UUID id,
        String eventType,
        String aggregateType,
        UUID aggregateId,
        String summary,
        Instant occurredAt,
        Instant recordedAt) {}
