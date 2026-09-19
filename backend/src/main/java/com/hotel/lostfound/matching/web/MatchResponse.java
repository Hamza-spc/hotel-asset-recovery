package com.hotel.lostfound.matching.web;

import com.hotel.lostfound.matching.MatchStatus;
import com.hotel.lostfound.matching.MatchSuggestion;
import java.time.Instant;
import java.util.UUID;

record MatchResponse(
        UUID id,
        UUID itemId,
        UUID reportId,
        String itemLabel,
        String reportLabel,
        double textScore,
        double spatialScore,
        double combinedScore,
        MatchStatus status,
        Instant createdAt) {

    static MatchResponse from(MatchSuggestion suggestion) {
        return new MatchResponse(
                suggestion.id(),
                suggestion.itemId(),
                suggestion.reportId(),
                suggestion.itemLabel(),
                suggestion.reportLabel(),
                suggestion.score().text(),
                suggestion.score().spatial(),
                suggestion.score().combined(),
                suggestion.status(),
                suggestion.createdAt());
    }
}
