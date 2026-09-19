package com.hotel.lostfound.claims;

import java.time.Instant;
import java.util.UUID;
import org.springframework.modulith.events.Externalized;

@Externalized("lostfound.claims::#{#this.reportId()}")
public record LossReportClosed(UUID reportId, Instant occurredAt) {}
