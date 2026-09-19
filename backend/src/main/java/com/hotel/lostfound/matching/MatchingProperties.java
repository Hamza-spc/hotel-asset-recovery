package com.hotel.lostfound.matching;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.matching")
public record MatchingProperties(
        String ollamaUrl,
        String model,
        double textWeight,
        double spatialWeight,
        double minScore,
        double spatialScale) {

    public MatchingProperties {
        if (ollamaUrl == null || ollamaUrl.isBlank()) {
            ollamaUrl = "http://localhost:11434";
        }
        if (model == null || model.isBlank()) {
            model = "nomic-embed-text";
        }
        if (textWeight == 0 && spatialWeight == 0) {
            textWeight = 0.7;
            spatialWeight = 0.3;
        }
        if (minScore == 0) {
            minScore = 0.52;
        }
        if (spatialScale == 0) {
            spatialScale = 40;
        }
    }
}
