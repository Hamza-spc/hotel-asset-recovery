package com.hotel.lostfound.matching.application;

import com.hotel.lostfound.claims.LossReportFiled;
import com.hotel.lostfound.claims.ReportMatchingPort;
import com.hotel.lostfound.claims.ReportSnapshot;
import com.hotel.lostfound.inventory.ItemLogged;
import com.hotel.lostfound.inventory.ItemMatchingPort;
import com.hotel.lostfound.inventory.ItemSnapshot;
import com.hotel.lostfound.matching.EmbeddingClient;
import com.hotel.lostfound.matching.MatchException;
import com.hotel.lostfound.matching.MatchRepository;
import com.hotel.lostfound.matching.MatchSuggestion;
import com.hotel.lostfound.matching.MatchingProperties;
import com.hotel.lostfound.matching.persistence.JdbcEmbeddingStore;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MatchingService {

    private final EmbeddingClient embeddings;
    private final JdbcEmbeddingStore vectors;
    private final MatchRepository matches;
    private final ItemMatchingPort items;
    private final ReportMatchingPort reports;
    private final MatchingProperties properties;
    private final ApplicationEventPublisher events;
    private final Clock clock;

    MatchingService(
            EmbeddingClient embeddings,
            JdbcEmbeddingStore vectors,
            MatchRepository matches,
            ItemMatchingPort items,
            ReportMatchingPort reports,
            MatchingProperties properties,
            ApplicationEventPublisher events,
            Clock clock) {
        this.embeddings = embeddings;
        this.vectors = vectors;
        this.matches = matches;
        this.items = items;
        this.reports = reports;
        this.properties = properties;
        this.events = events;
        this.clock = clock;
    }

    @ApplicationModuleListener
    void onItemLogged(ItemLogged event) {
        vectors.upsertItem(
                event.itemId(),
                embeddings.embed(event.description()),
                event.description(),
                event.mapX(),
                event.mapY());
        proposeForItem(event.itemId());
    }

    @ApplicationModuleListener
    void onReportFiled(LossReportFiled event) {
        vectors.upsertReport(
                event.reportId(),
                embeddings.embed(event.description()),
                event.description(),
                event.mapX(),
                event.mapY());
        proposeForReport(event.reportId());
    }

    @Transactional
    public List<MatchSuggestion> pending() {
        indexMissing();
        return matches.findPending();
    }

    @Transactional
    public MatchSuggestion accept(UUID id) {
        MatchSuggestion suggestion = require(id);
        Instant now = Instant.now(clock);
        suggestion.accept(now);
        publishAndSave(suggestion);
        items.openClaimFromMatch(suggestion.itemId());
        reports.resolveFromMatch(suggestion.reportId());
        for (MatchSuggestion other : matches.findPendingFor(suggestion.itemId(), suggestion.reportId())) {
            if (other.id().equals(suggestion.id())) {
                continue;
            }
            other.reject(now);
            publishAndSave(other);
            if (!other.itemId().equals(suggestion.itemId())) {
                items.revertMatch(other.itemId());
            }
            if (!other.reportId().equals(suggestion.reportId())) {
                reports.reopen(other.reportId());
            }
        }
        return suggestion;
    }

    @Transactional
    public MatchSuggestion reject(UUID id) {
        MatchSuggestion suggestion = require(id);
        suggestion.reject(Instant.now(clock));
        publishAndSave(suggestion);
        boolean itemStillPending = matches.findPending().stream()
                .anyMatch(pending -> pending.itemId().equals(suggestion.itemId()));
        boolean reportStillPending = matches.findPending().stream()
                .anyMatch(pending -> pending.reportId().equals(suggestion.reportId()));
        if (!itemStillPending) {
            items.revertMatch(suggestion.itemId());
        }
        if (!reportStillPending) {
            reports.reopen(suggestion.reportId());
        }
        return suggestion;
    }

    private void indexMissing() {
        for (ItemSnapshot item : items.snapshots()) {
            if (item.availableForMatch() && !vectors.hasItem(item.id())) {
                vectors.upsertItem(
                        item.id(), embeddings.embed(item.description()), item.description(), item.mapX(), item.mapY());
                proposeForItem(item.id());
            }
        }
        for (ReportSnapshot report : reports.snapshots()) {
            if (report.availableForMatch() && !vectors.hasReport(report.id())) {
                vectors.upsertReport(
                        report.id(),
                        embeddings.embed(report.description()),
                        report.description(),
                        report.mapX(),
                        report.mapY());
                proposeForReport(report.id());
            }
        }
    }

    private void proposeForItem(UUID itemId) {
        Map<UUID, ItemSnapshot> itemById = indexItems();
        Map<UUID, ReportSnapshot> reportById = indexReports();
        ItemSnapshot item = itemById.get(itemId);
        if (item == null || !item.availableForMatch()) {
            return;
        }
        for (var pair : vectors.findForItem(itemId)) {
            ReportSnapshot report = reportById.get(pair.reportId());
            if (report != null && report.availableForMatch()) {
                propose(item, report, pair.score());
            }
        }
    }

    private void proposeForReport(UUID reportId) {
        Map<UUID, ItemSnapshot> itemById = indexItems();
        Map<UUID, ReportSnapshot> reportById = indexReports();
        ReportSnapshot report = reportById.get(reportId);
        if (report == null || !report.availableForMatch()) {
            return;
        }
        for (var pair : vectors.findForReport(reportId)) {
            ItemSnapshot item = itemById.get(pair.itemId());
            if (item != null && item.availableForMatch()) {
                propose(item, report, pair.score());
            }
        }
    }

    private void propose(ItemSnapshot item, ReportSnapshot report, com.hotel.lostfound.matching.HybridScore score) {
        if (score.combined() < properties.minScore()) {
            return;
        }
        if (matches.findByPair(item.id(), report.id()).isPresent()) {
            return;
        }
        MatchSuggestion suggestion = MatchSuggestion.propose(
                item.id(),
                report.id(),
                item.trackingCode() + " · " + item.description(),
                report.guestName() + " · " + report.description(),
                score,
                Instant.now(clock));
        publishAndSave(suggestion);
        items.markMatchSuggested(item.id());
        reports.markMatched(report.id());
    }

    private Map<UUID, ItemSnapshot> indexItems() {
        return items.snapshots().stream().collect(Collectors.toMap(ItemSnapshot::id, Function.identity()));
    }

    private Map<UUID, ReportSnapshot> indexReports() {
        return reports.snapshots().stream().collect(Collectors.toMap(ReportSnapshot::id, Function.identity()));
    }

    private MatchSuggestion require(UUID id) {
        return matches.findById(id).orElseThrow(() -> new MatchException("Match not found: " + id));
    }

    private MatchSuggestion publishAndSave(MatchSuggestion suggestion) {
        var pending = suggestion.pullEvents();
        MatchSuggestion saved = matches.save(suggestion);
        pending.forEach(events::publishEvent);
        return saved;
    }
}
