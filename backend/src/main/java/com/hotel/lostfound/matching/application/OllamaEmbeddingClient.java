package com.hotel.lostfound.matching.application;

import com.hotel.lostfound.matching.EmbeddingClient;
import com.hotel.lostfound.matching.MatchException;
import com.hotel.lostfound.matching.MatchingProperties;
import java.util.List;
import java.util.Map;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
class OllamaEmbeddingClient implements EmbeddingClient {

    private final RestClient http;
    private final MatchingProperties properties;
    private volatile boolean modelReady;

    OllamaEmbeddingClient(MatchingProperties properties) {
        this.http = RestClient.builder().baseUrl(properties.ollamaUrl()).build();
        this.properties = properties;
    }

    @Override
    public float[] embed(String text) {
        ensureModel();
        try {
            EmbedResponse modern = http.post()
                    .uri("/api/embed")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("model", properties.model(), "input", text))
                    .retrieve()
                    .body(EmbedResponse.class);
            if (modern != null && modern.embeddings() != null && !modern.embeddings().isEmpty()) {
                return toArray(modern.embeddings().getFirst());
            }
        } catch (RuntimeException ignored) {
            // Older Ollama builds only expose /api/embeddings.
        }
        LegacyEmbedResponse legacy = http.post()
                .uri("/api/embeddings")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("model", properties.model(), "prompt", text))
                .retrieve()
                .body(LegacyEmbedResponse.class);
        if (legacy == null || legacy.embedding() == null || legacy.embedding().isEmpty()) {
            throw new MatchException("Ollama returned an empty embedding");
        }
        return toArray(legacy.embedding());
    }

    private void ensureModel() {
        if (modelReady) {
            return;
        }
        synchronized (this) {
            if (modelReady) {
                return;
            }
            try {
                http.post()
                        .uri("/api/pull")
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(Map.of("name", properties.model(), "stream", false))
                        .retrieve()
                        .toBodilessEntity();
                modelReady = true;
            } catch (RuntimeException ex) {
                throw new MatchException("Ollama is not ready for model " + properties.model(), ex);
            }
        }
    }

    private static float[] toArray(List<Double> values) {
        float[] embedding = new float[values.size()];
        for (int i = 0; i < values.size(); i++) {
            embedding[i] = values.get(i).floatValue();
        }
        return embedding;
    }

    private record EmbedResponse(List<List<Double>> embeddings) {}

    private record LegacyEmbedResponse(List<Double> embedding) {}
}
