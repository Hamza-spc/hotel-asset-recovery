package com.hotel.lostfound.audit.persistence;

import com.hotel.lostfound.audit.AuditEntry;
import com.hotel.lostfound.audit.AuditLog;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

@Repository
class JpaAuditLog implements AuditLog {

    private final AuditLogJpaRepository jpa;

    JpaAuditLog(AuditLogJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public AuditEntry append(AuditEntry entry) {
        AuditLogEntity saved = jpa.save(new AuditLogEntity(
                entry.id(),
                entry.eventType(),
                entry.aggregateType(),
                entry.aggregateId(),
                entry.summary(),
                entry.occurredAt(),
                entry.recordedAt()));
        return toDomain(saved);
    }

    @Override
    public List<AuditEntry> recent() {
        return jpa.findAll(Sort.by(Sort.Direction.DESC, "occurredAt")).stream()
                .map(JpaAuditLog::toDomain)
                .toList();
    }

    private static AuditEntry toDomain(AuditLogEntity entity) {
        return new AuditEntry(
                entity.getId(),
                entity.getEventType(),
                entity.getAggregateType(),
                entity.getAggregateId(),
                entity.getSummary(),
                entity.getOccurredAt(),
                entity.getRecordedAt());
    }
}
