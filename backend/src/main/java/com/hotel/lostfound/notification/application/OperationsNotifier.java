package com.hotel.lostfound.notification.application;

import com.hotel.lostfound.claims.LossReportClosed;
import com.hotel.lostfound.claims.LossReportFiled;
import com.hotel.lostfound.inventory.ItemClaimOpened;
import com.hotel.lostfound.inventory.ItemDisposed;
import com.hotel.lostfound.inventory.ItemLogged;
import com.hotel.lostfound.inventory.ItemReclaimed;
import com.hotel.lostfound.inventory.ItemStored;
import com.hotel.lostfound.inventory.ItemUnclaimed;
import com.hotel.lostfound.matching.MatchAccepted;
import com.hotel.lostfound.matching.MatchRejected;
import com.hotel.lostfound.matching.MatchSuggested;
import com.hotel.lostfound.notification.OperationsNotice;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Service;

@Service
public class OperationsNotifier {

    static final String TOPIC = "/topic/operations";

    private final SimpMessagingTemplate broker;

    OperationsNotifier(SimpMessagingTemplate broker) {
        this.broker = broker;
    }

    @ApplicationModuleListener
    void onLogged(ItemLogged event) {
        send(new OperationsNotice(
                "ITEM_LOGGED", "FOUND_ITEM", event.itemId(), "Logged " + event.trackingCode(), event.occurredAt()));
    }

    @ApplicationModuleListener
    void onStored(ItemStored event) {
        send(new OperationsNotice(
                "ITEM_STORED", "FOUND_ITEM", event.itemId(), "Stored at " + event.storageLocation(), event.occurredAt()));
    }

    @ApplicationModuleListener
    void onClaimOpened(ItemClaimOpened event) {
        send(new OperationsNotice(
                "ITEM_CLAIM_OPENED", "FOUND_ITEM", event.itemId(), "Claim opened", event.occurredAt()));
    }

    @ApplicationModuleListener
    void onReclaimed(ItemReclaimed event) {
        send(new OperationsNotice(
                "ITEM_RECLAIMED", "FOUND_ITEM", event.itemId(), "Handed back to guest", event.occurredAt()));
    }

    @ApplicationModuleListener
    void onUnclaimed(ItemUnclaimed event) {
        send(new OperationsNotice(
                "ITEM_UNCLAIMED", "FOUND_ITEM", event.itemId(), "Marked unclaimed", event.occurredAt()));
    }

    @ApplicationModuleListener
    void onDisposed(ItemDisposed event) {
        send(new OperationsNotice("ITEM_DISPOSED", "FOUND_ITEM", event.itemId(), "Disposed", event.occurredAt()));
    }

    @ApplicationModuleListener
    void onFiled(LossReportFiled event) {
        send(new OperationsNotice(
                "LOSS_REPORT_FILED",
                "LOSS_REPORT",
                event.reportId(),
                "Loss report for " + event.guestName(),
                event.occurredAt()));
    }

    @ApplicationModuleListener
    void onClosed(LossReportClosed event) {
        send(new OperationsNotice(
                "LOSS_REPORT_CLOSED", "LOSS_REPORT", event.reportId(), "Loss report closed", event.occurredAt()));
    }

    @ApplicationModuleListener
    void onMatchSuggested(MatchSuggested event) {
        send(new OperationsNotice(
                "MATCH_SUGGESTED", "MATCH", event.suggestionId(), "Match score " + event.score(), event.occurredAt()));
    }

    @ApplicationModuleListener
    void onMatchAccepted(MatchAccepted event) {
        send(new OperationsNotice("MATCH_ACCEPTED", "MATCH", event.suggestionId(), "Match accepted", event.occurredAt()));
    }

    @ApplicationModuleListener
    void onMatchRejected(MatchRejected event) {
        send(new OperationsNotice("MATCH_REJECTED", "MATCH", event.suggestionId(), "Match rejected", event.occurredAt()));
    }

    private void send(OperationsNotice notice) {
        broker.convertAndSend(TOPIC, notice);
    }
}
