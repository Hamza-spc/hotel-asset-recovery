package com.hotel.lostfound.matching;

public class HybridScorer {

    private final double textWeight;
    private final double spatialWeight;
    private final double spatialScale;

    public HybridScorer(double textWeight, double spatialWeight, double spatialScale) {
        this.textWeight = textWeight;
        this.spatialWeight = spatialWeight;
        this.spatialScale = spatialScale;
    }

    public HybridScore score(double cosineSimilarity, Double x1, Double y1, Double x2, Double y2) {
        double text = clamp(cosineSimilarity);
        double spatial = spatialScore(x1, y1, x2, y2);
        return new HybridScore(round(text), round(spatial), round(textWeight * text + spatialWeight * spatial));
    }

    double spatialScore(Double x1, Double y1, Double x2, Double y2) {
        if (x1 == null || y1 == null || x2 == null || y2 == null) {
            return 0.5;
        }
        double distance = Math.hypot(x1 - x2, y1 - y2);
        return clamp(1.0 - Math.min(distance / spatialScale, 1.0));
    }

    private static double clamp(double value) {
        return Math.max(0.0, Math.min(1.0, value));
    }

    private static double round(double value) {
        return Math.round(value * 1000.0) / 1000.0;
    }
}
