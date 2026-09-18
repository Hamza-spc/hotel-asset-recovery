package com.hotel.lostfound.inventory.persistence;

import com.hotel.lostfound.inventory.FoundItem;
import com.hotel.lostfound.inventory.FoundItemRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

@Repository
class JpaFoundItemRepository implements FoundItemRepository {

    private final FoundItemJpaRepository jpa;

    JpaFoundItemRepository(FoundItemJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public FoundItem save(FoundItem item) {
        FoundItemEntity entity = new FoundItemEntity(
                item.id(),
                item.trackingCode(),
                item.description(),
                item.category(),
                item.status(),
                item.photoObjectKey().orElse(null),
                item.zoneName(),
                item.storageLocation().orElse(null),
                item.foundBy(),
                item.foundAt(),
                item.version());
        return toDomain(jpa.save(entity));
    }

    @Override
    public Optional<FoundItem> findById(UUID id) {
        return jpa.findById(id).map(JpaFoundItemRepository::toDomain);
    }

    @Override
    public List<FoundItem> findAll() {
        return jpa.findAll(Sort.by(Sort.Direction.DESC, "foundAt")).stream()
                .map(JpaFoundItemRepository::toDomain)
                .toList();
    }

    private static FoundItem toDomain(FoundItemEntity entity) {
        return FoundItem.rehydrate(
                entity.getId(),
                entity.getTrackingCode(),
                entity.getDescription(),
                entity.getCategory(),
                entity.getStatus(),
                entity.getPhotoObjectKey(),
                entity.getZoneName(),
                entity.getStorageLocation(),
                entity.getFoundBy(),
                entity.getFoundAt(),
                entity.getVersion());
    }
}
