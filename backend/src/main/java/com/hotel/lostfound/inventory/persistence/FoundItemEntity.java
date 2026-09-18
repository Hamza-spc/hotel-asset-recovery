package com.hotel.lostfound.inventory.persistence;

import com.hotel.lostfound.inventory.ItemCategory;
import com.hotel.lostfound.inventory.ItemStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "found_item")
class FoundItemEntity {

    @Id
    private UUID id;

    @Column(name = "tracking_code", nullable = false, unique = true)
    private String trackingCode;

    @Column(nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ItemCategory category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ItemStatus status;

    @Column(name = "photo_object_key")
    private String photoObjectKey;

    @Column(name = "zone_name", nullable = false)
    private String zoneName;

    @Column(name = "storage_location")
    private String storageLocation;

    @Column(name = "found_by", nullable = false)
    private String foundBy;

    @Column(name = "found_at", nullable = false)
    private Instant foundAt;

    @Version
    private long version;

    protected FoundItemEntity() {}

    FoundItemEntity(
            UUID id,
            String trackingCode,
            String description,
            ItemCategory category,
            ItemStatus status,
            String photoObjectKey,
            String zoneName,
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
        this.storageLocation = storageLocation;
        this.foundBy = foundBy;
        this.foundAt = foundAt;
        this.version = version;
    }

    UUID getId() {
        return id;
    }

    String getTrackingCode() {
        return trackingCode;
    }

    String getDescription() {
        return description;
    }

    ItemCategory getCategory() {
        return category;
    }

    ItemStatus getStatus() {
        return status;
    }

    String getPhotoObjectKey() {
        return photoObjectKey;
    }

    String getZoneName() {
        return zoneName;
    }

    String getStorageLocation() {
        return storageLocation;
    }

    String getFoundBy() {
        return foundBy;
    }

    Instant getFoundAt() {
        return foundAt;
    }

    long getVersion() {
        return version;
    }
}
