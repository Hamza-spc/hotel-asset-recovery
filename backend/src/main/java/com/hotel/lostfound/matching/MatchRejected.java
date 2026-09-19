package com.hotel.lostfound.matching;

import java.time.Instant;
import java.util.UUID;
import org.springframework.modulith.events.Externalized;

@Externalized("lostfound.matching::#{#this.suggestionId()}")
public record MatchRejected(UUID suggestionId, UUID itemId, UUID reportId, Instant occurredAt) {}
