package com.hotel.lostfound.inventory;

import java.util.EnumSet;
import java.util.Set;

public enum ItemStatus {
    LOGGED,
    STORED,
    MATCH_SUGGESTED,
    CLAIM_PENDING,
    RECLAIMED,
    UNCLAIMED,
    DISPOSED;

    public boolean canTransitionTo(ItemStatus next) {
        return allowedTargets().contains(next);
    }

    private Set<ItemStatus> allowedTargets() {
        return switch (this) {
            case LOGGED -> EnumSet.of(STORED);
            case STORED -> EnumSet.of(MATCH_SUGGESTED, CLAIM_PENDING, UNCLAIMED);
            case MATCH_SUGGESTED -> EnumSet.of(CLAIM_PENDING, STORED);
            case CLAIM_PENDING -> EnumSet.of(RECLAIMED, STORED);
            case UNCLAIMED -> EnumSet.of(DISPOSED, CLAIM_PENDING);
            case RECLAIMED, DISPOSED -> EnumSet.noneOf(ItemStatus.class);
        };
    }
}
