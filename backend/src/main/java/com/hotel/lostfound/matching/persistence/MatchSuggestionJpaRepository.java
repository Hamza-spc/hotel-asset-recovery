package com.hotel.lostfound.matching.persistence;

import com.hotel.lostfound.matching.MatchStatus;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

interface MatchSuggestionJpaRepository extends JpaRepository<MatchSuggestionEntity, UUID> {

    Optional<MatchSuggestionEntity> findByItemIdAndReportId(UUID itemId, UUID reportId);

    List<MatchSuggestionEntity> findByStatusOrderByCombinedScoreDesc(MatchStatus status);

    @Query(
            """
            SELECT m FROM MatchSuggestionEntity m
            WHERE m.status = :status AND (m.itemId = :itemId OR m.reportId = :reportId)
            """)
    List<MatchSuggestionEntity> findPendingFor(
            @Param("status") MatchStatus status, @Param("itemId") UUID itemId, @Param("reportId") UUID reportId);
}
