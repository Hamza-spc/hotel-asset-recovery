package com.hotel.lostfound.matching;

import java.time.Instant;
import java.util.UUID;
import org.springframework.modulith.events.Externalized;

@Externalized("lostfound.matching::#{#this.suggestionId()}")
public record MatchSuggested(UUID suggestionId, UUID itemId, UUID reportId, double score, Instant occurredAt) {}
