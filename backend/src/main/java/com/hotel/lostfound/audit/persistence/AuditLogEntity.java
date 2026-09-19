package com.hotel.lostfound.audit.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "audit_log")
class AuditLogEntity {

    @Id
    private UUID id;

    @Column(name = "event_type", nullable = false)
    private String eventType;

    @Column(name = "aggregate_type", nullable = false)
    private String aggregateType;

    @Column(name = "aggregate_id", nullable = false)
    private UUID aggregateId;

    @Column(nullable = false)
    private String summary;

    @Column(name = "occurred_at", nullable = false)
    private Instant occurredAt;

    @Column(name = "recorded_at", nullable = false)
    private Instant recordedAt;

    protected AuditLogEntity() {}

    AuditLogEntity(
            UUID id,
            String eventType,
            String aggregateType,
            UUID aggregateId,
            String summary,
            Instant occurredAt,
            Instant recordedAt) {
        this.id = id;
        this.eventType = eventType;
        this.aggregateType = aggregateType;
        this.aggregateId = aggregateId;
        this.summary = summary;
        this.occurredAt = occurredAt;
        this.recordedAt = recordedAt;
    }

    UUID getId() {
        return id;
    }

    String getEventType() {
        return eventType;
    }

    String getAggregateType() {
        return aggregateType;
    }

    UUID getAggregateId() {
        return aggregateId;
    }

    String getSummary() {
        return summary;
    }

    Instant getOccurredAt() {
        return occurredAt;
    }

    Instant getRecordedAt() {
        return recordedAt;
    }
}
