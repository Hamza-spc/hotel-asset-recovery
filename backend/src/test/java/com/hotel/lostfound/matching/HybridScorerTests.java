package com.hotel.lostfound.matching;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class HybridScorerTests {

    private final HybridScorer scorer = new HybridScorer(0.7, 0.3, 40);

    @Test
    void nearbySimilarTextOutranksDistantSameText() {
        HybridScore lobby = scorer.score(0.92, 20.0, 55.0, 22.0, 54.0);
        HybridScore pool = scorer.score(0.92, 20.0, 55.0, 90.0, 50.0);
        assertTrue(lobby.combined() > pool.combined());
        assertTrue(lobby.spatial() > pool.spatial());
    }

    @Test
    void missingCoordinatesUseNeutralSpatialScore() {
        HybridScore score = scorer.score(0.8, null, null, 20.0, 55.0);
        assertEquals(0.5, score.spatial());
        assertEquals(0.71, score.combined());
    }
}
