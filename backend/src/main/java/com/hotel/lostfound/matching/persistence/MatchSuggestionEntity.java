package com.hotel.lostfound.matching.persistence;

import com.hotel.lostfound.matching.MatchStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "match_suggestion")
class MatchSuggestionEntity {

    @Id
    private UUID id;

    @Column(name = "item_id", nullable = false)
    private UUID itemId;

    @Column(name = "report_id", nullable = false)
    private UUID reportId;

    @Column(name = "item_label", nullable = false)
    private String itemLabel;

    @Column(name = "report_label", nullable = false)
    private String reportLabel;

    @Column(name = "text_score", nullable = false)
    private double textScore;

    @Column(name = "spatial_score", nullable = false)
    private double spatialScore;

    @Column(name = "combined_score", nullable = false)
    private double combinedScore;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MatchStatus status;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected MatchSuggestionEntity() {}

    MatchSuggestionEntity(
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
        this.id = id;
        this.itemId = itemId;
        this.reportId = reportId;
        this.itemLabel = itemLabel;
        this.reportLabel = reportLabel;
        this.textScore = textScore;
        this.spatialScore = spatialScore;
        this.combinedScore = combinedScore;
        this.status = status;
        this.createdAt = createdAt;
    }

    UUID getId() {
        return id;
    }

    UUID getItemId() {
        return itemId;
    }

    UUID getReportId() {
        return reportId;
    }

    String getItemLabel() {
        return itemLabel;
    }

    String getReportLabel() {
        return reportLabel;
    }

    double getTextScore() {
        return textScore;
    }

    double getSpatialScore() {
        return spatialScore;
    }

    double getCombinedScore() {
        return combinedScore;
    }

    MatchStatus getStatus() {
        return status;
    }

    Instant getCreatedAt() {
        return createdAt;
    }
}
