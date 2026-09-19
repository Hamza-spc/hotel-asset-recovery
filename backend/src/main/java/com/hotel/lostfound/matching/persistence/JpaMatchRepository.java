package com.hotel.lostfound.matching.persistence;

import com.hotel.lostfound.matching.HybridScore;
import com.hotel.lostfound.matching.MatchRepository;
import com.hotel.lostfound.matching.MatchStatus;
import com.hotel.lostfound.matching.MatchSuggestion;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
class JpaMatchRepository implements MatchRepository {

    private final MatchSuggestionJpaRepository jpa;

    JpaMatchRepository(MatchSuggestionJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public MatchSuggestion save(MatchSuggestion suggestion) {
        MatchSuggestionEntity entity = new MatchSuggestionEntity(
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
        return toDomain(jpa.save(entity));
    }

    @Override
    public Optional<MatchSuggestion> findById(UUID id) {
        return jpa.findById(id).map(JpaMatchRepository::toDomain);
    }

    @Override
    public Optional<MatchSuggestion> findByPair(UUID itemId, UUID reportId) {
        return jpa.findByItemIdAndReportId(itemId, reportId).map(JpaMatchRepository::toDomain);
    }

    @Override
    public List<MatchSuggestion> findPending() {
        return jpa.findByStatusOrderByCombinedScoreDesc(MatchStatus.PENDING).stream()
                .map(JpaMatchRepository::toDomain)
                .toList();
    }

    @Override
    public List<MatchSuggestion> findPendingFor(UUID itemId, UUID reportId) {
        return jpa.findPendingFor(MatchStatus.PENDING, itemId, reportId)
                .stream()
                .map(JpaMatchRepository::toDomain)
                .toList();
    }

    private static MatchSuggestion toDomain(MatchSuggestionEntity entity) {
        return MatchSuggestion.rehydrate(
                entity.getId(),
                entity.getItemId(),
                entity.getReportId(),
                entity.getItemLabel(),
                entity.getReportLabel(),
                new HybridScore(entity.getTextScore(), entity.getSpatialScore(), entity.getCombinedScore()),
                entity.getStatus(),
                entity.getCreatedAt());
    }
}
