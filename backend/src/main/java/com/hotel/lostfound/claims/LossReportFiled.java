package com.hotel.lostfound.claims;

import java.time.Instant;
import java.util.UUID;
import org.springframework.modulith.events.Externalized;

@Externalized("lostfound.claims::#{#this.reportId()}")
public record LossReportFiled(
        UUID reportId,
        String guestName,
        String description,
        String zoneName,
        Double mapX,
        Double mapY,
        Instant occurredAt) {}
