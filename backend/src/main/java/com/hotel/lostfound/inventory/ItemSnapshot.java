package com.hotel.lostfound.inventory;

import java.util.UUID;

public record ItemSnapshot(
        UUID id, String trackingCode, String description, Double mapX, Double mapY, ItemStatus status) {

    public boolean availableForMatch() {
        return status == ItemStatus.LOGGED || status == ItemStatus.STORED || status == ItemStatus.MATCH_SUGGESTED;
    }
}
