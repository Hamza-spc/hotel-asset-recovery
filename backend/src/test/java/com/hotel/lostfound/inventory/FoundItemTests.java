package com.hotel.lostfound.inventory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import org.junit.jupiter.api.Test;

class FoundItemTests {

    @Test
    void loggedItemCannotBeReclaimedDirectly() {
        FoundItem item = sample();
        assertThrows(ItemLifecycleException.class, item::reclaim);
        assertEquals(ItemStatus.LOGGED, item.status());
    }

    @Test
    void storageThenClaimThenReclaim() {
        FoundItem item = sample();
        item.moveToStorage("Shelf B4");
        item.openClaim();
        item.reclaim();
        assertEquals(ItemStatus.RECLAIMED, item.status());
        assertTrue(item.pullEvents().stream().anyMatch(ItemReclaimed.class::isInstance));
    }

    @Test
    void disposeRequiresUnclaimed() {
        FoundItem item = sample();
        item.moveToStorage("Cage 2");
        assertThrows(ItemLifecycleException.class, item::dispose);
        item.markUnclaimed();
        item.dispose();
        assertEquals(ItemStatus.DISPOSED, item.status());
    }

    private static FoundItem sample() {
        return FoundItem.log(
                "LF-2026-0001",
                "Black leather wallet",
                ItemCategory.WALLET,
                "Lobby",
                new MapPoint(20, 55),
                null,
                "housekeeper",
                Instant.parse("2026-09-19T00:00:00Z"));
    }
}
