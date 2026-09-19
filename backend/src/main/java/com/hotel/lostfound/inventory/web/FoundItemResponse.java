package com.hotel.lostfound.inventory.web;

import com.hotel.lostfound.inventory.FoundItem;
import com.hotel.lostfound.inventory.ItemCategory;
import com.hotel.lostfound.inventory.ItemStatus;
import java.time.Instant;
import java.util.UUID;

record FoundItemResponse(
        UUID id,
        String trackingCode,
        String description,
        ItemCategory category,
        ItemStatus status,
        String zoneName,
        Double mapX,
        Double mapY,
        String storageLocation,
        boolean hasPhoto,
        String foundBy,
        Instant foundAt) {

    static FoundItemResponse from(FoundItem item) {
        return new FoundItemResponse(
                item.id(),
                item.trackingCode(),
                item.description(),
                item.category(),
                item.status(),
                item.zoneName(),
                item.mapPoint().map(com.hotel.lostfound.inventory.MapPoint::x).orElse(null),
                item.mapPoint().map(com.hotel.lostfound.inventory.MapPoint::y).orElse(null),
                item.storageLocation().orElse(null),
                item.photoObjectKey().isPresent(),
                item.foundBy(),
                item.foundAt());
    }
}
