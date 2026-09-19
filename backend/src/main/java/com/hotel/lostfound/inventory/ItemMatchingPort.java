package com.hotel.lostfound.inventory;

import java.util.List;
import java.util.UUID;

public interface ItemMatchingPort {

    List<ItemSnapshot> snapshots();

    void markMatchSuggested(UUID itemId);

    void openClaimFromMatch(UUID itemId);

    void revertMatch(UUID itemId);
}
