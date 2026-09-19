package com.hotel.lostfound.audit.application;

import com.hotel.lostfound.audit.AuditEntry;
import com.hotel.lostfound.audit.AuditLog;
import com.hotel.lostfound.claims.LossReportClosed;
import com.hotel.lostfound.claims.LossReportFiled;
import com.hotel.lostfound.inventory.ItemClaimOpened;
import com.hotel.lostfound.inventory.ItemDisposed;
import com.hotel.lostfound.inventory.ItemLogged;
import com.hotel.lostfound.inventory.ItemReclaimed;
import com.hotel.lostfound.inventory.ItemStored;
import com.hotel.lostfound.inventory.ItemUnclaimed;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuditService {

    private final AuditLog log;
    private final Clock clock;

    AuditService(AuditLog log, Clock clock) {
        this.log = log;
        this.clock = clock;
    }

    @Transactional(readOnly = true)
    public List<AuditEntry> recent() {
        return log.recent();
    }

    @ApplicationModuleListener
    void onLogged(ItemLogged event) {
        append("ITEM_LOGGED", "FOUND_ITEM", event.itemId(), "Logged " + event.trackingCode(), event.occurredAt());
    }

    @ApplicationModuleListener
    void onStored(ItemStored event) {
        append("ITEM_STORED", "FOUND_ITEM", event.itemId(), "Moved to " + event.storageLocation(), event.occurredAt());
    }

    @ApplicationModuleListener
    void onClaimOpened(ItemClaimOpened event) {
        append("ITEM_CLAIM_OPENED", "FOUND_ITEM", event.itemId(), "Claim opened", event.occurredAt());
    }

    @ApplicationModuleListener
    void onReclaimed(ItemReclaimed event) {
        append("ITEM_RECLAIMED", "FOUND_ITEM", event.itemId(), "Handed back to guest", event.occurredAt());
    }

    @ApplicationModuleListener
    void onUnclaimed(ItemUnclaimed event) {
        append("ITEM_UNCLAIMED", "FOUND_ITEM", event.itemId(), "Marked unclaimed", event.occurredAt());
    }

    @ApplicationModuleListener
    void onDisposed(ItemDisposed event) {
        append("ITEM_DISPOSED", "FOUND_ITEM", event.itemId(), "Disposed", event.occurredAt());
    }

    @ApplicationModuleListener
    void onFiled(LossReportFiled event) {
        append("LOSS_REPORT_FILED", "LOSS_REPORT", event.reportId(), "Filed for " + event.guestName(), event.occurredAt());
    }

    @ApplicationModuleListener
    void onClosed(LossReportClosed event) {
        append("LOSS_REPORT_CLOSED", "LOSS_REPORT", event.reportId(), "Closed", event.occurredAt());
    }

    private void append(String eventType, String aggregateType, UUID aggregateId, String summary, Instant occurredAt) {
        log.append(new AuditEntry(
                UUID.randomUUID(), eventType, aggregateType, aggregateId, summary, occurredAt, Instant.now(clock)));
    }
}
