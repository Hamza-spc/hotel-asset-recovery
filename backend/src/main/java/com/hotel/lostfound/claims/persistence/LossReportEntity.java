package com.hotel.lostfound.claims.persistence;

import com.hotel.lostfound.claims.LossReportStatus;
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
@Table(name = "loss_report")
class LossReportEntity {

    @Id
    private UUID id;

    @Column(name = "guest_name", nullable = false)
    private String guestName;

    @Column(name = "room_number", nullable = false)
    private String roomNumber;

    @Column(nullable = false)
    private String contact;

    @Column(nullable = false)
    private String description;

    @Column(name = "photo_object_key")
    private String photoObjectKey;

    @Column(name = "zone_name", nullable = false)
    private String zoneName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LossReportStatus status;

    @Column(name = "filed_by", nullable = false)
    private String filedBy;

    @Column(name = "filed_at", nullable = false)
    private Instant filedAt;

    @Version
    private long version;

    protected LossReportEntity() {}

    LossReportEntity(
            UUID id,
            String guestName,
            String roomNumber,
            String contact,
            String description,
            String photoObjectKey,
            String zoneName,
            LossReportStatus status,
            String filedBy,
            Instant filedAt,
            long version) {
        this.id = id;
        this.guestName = guestName;
        this.roomNumber = roomNumber;
        this.contact = contact;
        this.description = description;
        this.photoObjectKey = photoObjectKey;
        this.zoneName = zoneName;
        this.status = status;
        this.filedBy = filedBy;
        this.filedAt = filedAt;
        this.version = version;
    }

    UUID getId() {
        return id;
    }

    String getGuestName() {
        return guestName;
    }

    String getRoomNumber() {
        return roomNumber;
    }

    String getContact() {
        return contact;
    }

    String getDescription() {
        return description;
    }

    String getPhotoObjectKey() {
        return photoObjectKey;
    }

    String getZoneName() {
        return zoneName;
    }

    LossReportStatus getStatus() {
        return status;
    }

    String getFiledBy() {
        return filedBy;
    }

    Instant getFiledAt() {
        return filedAt;
    }

    long getVersion() {
        return version;
    }
}
