package com.hotel.lostfound.matching;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class MatchSuggestionTests {

    @Test
    void acceptThenRejectIsRejected() {
        MatchSuggestion suggestion = MatchSuggestion.propose(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "LF-2026-0002 · wallet",
                "Ada · wallet",
                new HybridScore(0.9, 0.8, 0.87),
                Instant.parse("2026-09-19T00:00:00Z"));
        suggestion.accept(Instant.parse("2026-09-19T00:01:00Z"));
        assertEquals(MatchStatus.ACCEPTED, suggestion.status());
        assertTrue(suggestion.pullEvents().stream().anyMatch(MatchAccepted.class::isInstance));
        assertThrows(MatchException.class, () -> suggestion.reject(Instant.parse("2026-09-19T00:02:00Z")));
    }
}
