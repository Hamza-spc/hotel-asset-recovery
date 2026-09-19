package com.hotel.lostfound.inventory;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class FoundItem {

    private final UUID id;
    private final String trackingCode;
    private String description;
    private ItemCategory category;
    private ItemStatus status;
    private String photoObjectKey;
    private String zoneName;
    private final Double mapX;
    private final Double mapY;
    private String storageLocation;
    private final String foundBy;
    private final Instant foundAt;
    private long version;
    private final List<Object> events = new ArrayList<>();

    private FoundItem(
            UUID id,
            String trackingCode,
            String description,
            ItemCategory category,
            ItemStatus status,
            String photoObjectKey,
            String zoneName,
            Double mapX,
            Double mapY,
            String storageLocation,
            String foundBy,
            Instant foundAt,
            long version) {
        this.id = id;
        this.trackingCode = trackingCode;
        this.description = description;
        this.category = category;
        this.status = status;
        this.photoObjectKey = photoObjectKey;
        this.zoneName = zoneName;
        this.mapX = mapX;
        this.mapY = mapY;
        this.storageLocation = storageLocation;
        this.foundBy = foundBy;
        this.foundAt = foundAt;
        this.version = version;
    }

    public static FoundItem log(
            String trackingCode,
            String description,
            ItemCategory category,
            String zoneName,
            MapPoint point,
            String photoObjectKey,
            String foundBy,
            Instant foundAt) {
        FoundItem item = new FoundItem(
                UUID.randomUUID(),
                trackingCode,
                requireText(description, "description"),
                category,
                ItemStatus.LOGGED,
                photoObjectKey,
                requireText(zoneName, "zone"),
                point == null ? null : point.x(),
                point == null ? null : point.y(),
                null,
                requireText(foundBy, "foundBy"),
                foundAt,
                0);
        item.events.add(new ItemLogged(item.id, item.trackingCode, foundAt));
        return item;
    }

    public static FoundItem rehydrate(
            UUID id,
            String trackingCode,
            String description,
            ItemCategory category,
            ItemStatus status,
            String photoObjectKey,
            String zoneName,
            Double mapX,
            Double mapY,
            String storageLocation,
            String foundBy,
            Instant foundAt,
            long version) {
        return new FoundItem(
                id,
                trackingCode,
                description,
                category,
                status,
                photoObjectKey,
                zoneName,
                mapX,
                mapY,
                storageLocation,
                foundBy,
                foundAt,
                version);
    }

    public void moveToStorage(String location) {
        transitionTo(ItemStatus.STORED);
        this.storageLocation = requireText(location, "storageLocation");
        events.add(new ItemStored(id, this.storageLocation, Instant.now()));
    }

    public void openClaim() {
        transitionTo(ItemStatus.CLAIM_PENDING);
    }

    public void reclaim() {
        transitionTo(ItemStatus.RECLAIMED);
        events.add(new ItemReclaimed(id, Instant.now()));
    }

    public void markUnclaimed() {
        transitionTo(ItemStatus.UNCLAIMED);
    }

    public void dispose() {
        transitionTo(ItemStatus.DISPOSED);
        events.add(new ItemDisposed(id, Instant.now()));
    }

    public void attachPhoto(String objectKey) {
        this.photoObjectKey = requireText(objectKey, "photo");
    }

    private void transitionTo(ItemStatus next) {
        if (!status.canTransitionTo(next)) {
            throw new ItemLifecycleException("Cannot move item %s from %s to %s".formatted(trackingCode, status, next));
        }
        this.status = next;
    }

    public List<Object> pullEvents() {
        List<Object> copy = List.copyOf(events);
        events.clear();
        return copy;
    }

    public UUID id() {
        return id;
    }

    public String trackingCode() {
        return trackingCode;
    }

    public String description() {
        return description;
    }

    public ItemCategory category() {
        return category;
    }

    public ItemStatus status() {
        return status;
    }

    public Optional<String> photoObjectKey() {
        return Optional.ofNullable(photoObjectKey);
    }

    public String zoneName() {
        return zoneName;
    }

    public Optional<MapPoint> mapPoint() {
        if (mapX == null || mapY == null) {
            return Optional.empty();
        }
        return Optional.of(new MapPoint(mapX, mapY));
    }

    public Optional<String> storageLocation() {
        return Optional.ofNullable(storageLocation);
    }

    public String foundBy() {
        return foundBy;
    }

    public Instant foundAt() {
        return foundAt;
    }

    public long version() {
        return version;
    }

    private static String requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " is required");
        }
        return value.trim();
    }
}
