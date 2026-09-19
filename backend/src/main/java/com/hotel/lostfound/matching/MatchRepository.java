package com.hotel.lostfound.matching;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MatchRepository {

    MatchSuggestion save(MatchSuggestion suggestion);

    Optional<MatchSuggestion> findById(UUID id);

    Optional<MatchSuggestion> findByPair(UUID itemId, UUID reportId);

    List<MatchSuggestion> findPending();

    List<MatchSuggestion> findPendingFor(UUID itemId, UUID reportId);
}
