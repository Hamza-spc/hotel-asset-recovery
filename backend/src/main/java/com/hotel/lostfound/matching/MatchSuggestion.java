package com.hotel.lostfound.matching;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MatchSuggestion {

    private final UUID id;
    private final UUID itemId;
    private final UUID reportId;
    private final String itemLabel;
    private final String reportLabel;
    private final HybridScore score;
    private MatchStatus status;
    private final Instant createdAt;
    private final List<Object> events = new ArrayList<>();

    private MatchSuggestion(
            UUID id,
            UUID itemId,
            UUID reportId,
            String itemLabel,
            String reportLabel,
            HybridScore score,
            MatchStatus status,
            Instant createdAt) {
        this.id = id;
        this.itemId = itemId;
        this.reportId = reportId;
        this.itemLabel = itemLabel;
        this.reportLabel = reportLabel;
        this.score = score;
        this.status = status;
        this.createdAt = createdAt;
    }

    public static MatchSuggestion propose(
            UUID itemId, UUID reportId, String itemLabel, String reportLabel, HybridScore score, Instant createdAt) {
        MatchSuggestion suggestion = new MatchSuggestion(
                UUID.randomUUID(), itemId, reportId, itemLabel, reportLabel, score, MatchStatus.PENDING, createdAt);
        suggestion.events.add(new MatchSuggested(suggestion.id, itemId, reportId, score.combined(), createdAt));
        return suggestion;
    }

    public static MatchSuggestion rehydrate(
            UUID id,
            UUID itemId,
            UUID reportId,
            String itemLabel,
            String reportLabel,
            HybridScore score,
            MatchStatus status,
            Instant createdAt) {
        return new MatchSuggestion(id, itemId, reportId, itemLabel, reportLabel, score, status, createdAt);
    }

    public void accept(Instant at) {
        requirePending();
        status = MatchStatus.ACCEPTED;
        events.add(new MatchAccepted(id, itemId, reportId, at));
    }

    public void reject(Instant at) {
        requirePending();
        status = MatchStatus.REJECTED;
        events.add(new MatchRejected(id, itemId, reportId, at));
    }

    public List<Object> pullEvents() {
        List<Object> copy = List.copyOf(events);
        events.clear();
        return copy;
    }

    public UUID id() {
        return id;
    }

    public UUID itemId() {
        return itemId;
    }

    public UUID reportId() {
        return reportId;
    }

    public String itemLabel() {
        return itemLabel;
    }

    public String reportLabel() {
        return reportLabel;
    }

    public HybridScore score() {
        return score;
    }

    public MatchStatus status() {
        return status;
    }

    public Instant createdAt() {
        return createdAt;
    }

    private void requirePending() {
        if (status != MatchStatus.PENDING) {
            throw new MatchException("Match is already " + status);
        }
    }
}
