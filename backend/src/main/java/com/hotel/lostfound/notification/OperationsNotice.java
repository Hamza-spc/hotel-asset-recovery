package com.hotel.lostfound.notification;

import java.time.Instant;
import java.util.UUID;

public record OperationsNotice(
        String type, String aggregateType, UUID aggregateId, String summary, Instant occurredAt) {}
