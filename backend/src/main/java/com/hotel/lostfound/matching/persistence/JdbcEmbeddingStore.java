package com.hotel.lostfound.matching.persistence;

import com.hotel.lostfound.matching.HybridScore;
import com.hotel.lostfound.matching.HybridScorer;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcEmbeddingStore {

    private final JdbcTemplate jdbc;
    private final HybridScorer scorer;

    JdbcEmbeddingStore(JdbcTemplate jdbc, HybridScorer scorer) {
        this.jdbc = jdbc;
        this.scorer = scorer;
    }

    public void upsertItem(UUID itemId, float[] embedding, String description, Double mapX, Double mapY) {
        jdbc.update(
                """
                INSERT INTO item_embedding (item_id, embedding, description, map_x, map_y, embedded_at)
                VALUES (?, ?::vector, ?, ?, ?, ?)
                ON CONFLICT (item_id) DO UPDATE SET
                    embedding = EXCLUDED.embedding,
                    description = EXCLUDED.description,
                    map_x = EXCLUDED.map_x,
                    map_y = EXCLUDED.map_y,
                    embedded_at = EXCLUDED.embedded_at
                """,
                itemId,
                toLiteral(embedding),
                description,
                mapX,
                mapY,
                Timestamp.from(Instant.now()));
    }

    public void upsertReport(UUID reportId, float[] embedding, String description, Double mapX, Double mapY) {
        jdbc.update(
                """
                INSERT INTO report_embedding (report_id, embedding, description, map_x, map_y, embedded_at)
                VALUES (?, ?::vector, ?, ?, ?, ?)
                ON CONFLICT (report_id) DO UPDATE SET
                    embedding = EXCLUDED.embedding,
                    description = EXCLUDED.description,
                    map_x = EXCLUDED.map_x,
                    map_y = EXCLUDED.map_y,
                    embedded_at = EXCLUDED.embedded_at
                """,
                reportId,
                toLiteral(embedding),
                description,
                mapX,
                mapY,
                Timestamp.from(Instant.now()));
    }

    public boolean hasItem(UUID itemId) {
        Integer count = jdbc.queryForObject("SELECT COUNT(*) FROM item_embedding WHERE item_id = ?", Integer.class, itemId);
        return count != null && count > 0;
    }

    public boolean hasReport(UUID reportId) {
        Integer count =
                jdbc.queryForObject("SELECT COUNT(*) FROM report_embedding WHERE report_id = ?", Integer.class, reportId);
        return count != null && count > 0;
    }

    public List<RankedPair> findForReport(UUID reportId) {
        return jdbc.query(
                """
                SELECT i.item_id,
                       1 - (i.embedding <=> r.embedding) AS text_score,
                       i.map_x AS item_x, i.map_y AS item_y,
                       r.map_x AS report_x, r.map_y AS report_y
                FROM item_embedding i
                JOIN report_embedding r ON r.report_id = ?
                """,
                (rs, rowNum) -> ranked(
                        rs.getObject("item_id", UUID.class),
                        reportId,
                        rs.getDouble("text_score"),
                        (Double) rs.getObject("item_x"),
                        (Double) rs.getObject("item_y"),
                        (Double) rs.getObject("report_x"),
                        (Double) rs.getObject("report_y")),
                reportId);
    }

    public List<RankedPair> findForItem(UUID itemId) {
        return jdbc.query(
                """
                SELECT r.report_id,
                       1 - (r.embedding <=> i.embedding) AS text_score,
                       i.map_x AS item_x, i.map_y AS item_y,
                       r.map_x AS report_x, r.map_y AS report_y
                FROM report_embedding r
                JOIN item_embedding i ON i.item_id = ?
                """,
                (rs, rowNum) -> ranked(
                        itemId,
                        rs.getObject("report_id", UUID.class),
                        rs.getDouble("text_score"),
                        (Double) rs.getObject("item_x"),
                        (Double) rs.getObject("item_y"),
                        (Double) rs.getObject("report_x"),
                        (Double) rs.getObject("report_y")),
                itemId);
    }

    private RankedPair ranked(
            UUID itemId, UUID reportId, double textScore, Double itemX, Double itemY, Double reportX, Double reportY) {
        return new RankedPair(itemId, reportId, scorer.score(textScore, itemX, itemY, reportX, reportY));
    }

    private static String toLiteral(float[] embedding) {
        StringBuilder builder = new StringBuilder("[");
        for (int i = 0; i < embedding.length; i++) {
            if (i > 0) {
                builder.append(',');
            }
            builder.append(embedding[i]);
        }
        return builder.append(']').toString();
    }

    public record RankedPair(UUID itemId, UUID reportId, HybridScore score) {}
}
