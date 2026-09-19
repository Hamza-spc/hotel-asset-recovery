package com.hotel.lostfound.inventory.application;

import com.hotel.lostfound.inventory.FoundItem;
import com.hotel.lostfound.inventory.FoundItemRepository;
import com.hotel.lostfound.inventory.ItemCategory;
import com.hotel.lostfound.inventory.MapPoint;
import com.hotel.lostfound.inventory.ItemLifecycleException;
import com.hotel.lostfound.inventory.ObjectStorage;
import com.hotel.lostfound.inventory.TrackingCodeGenerator;
import java.io.IOException;
import java.io.InputStream;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class InventoryService {

    private final FoundItemRepository items;
    private final TrackingCodeGenerator trackingCodes;
    private final ObjectStorage storage;
    private final ApplicationEventPublisher events;
    private final Clock clock;

    InventoryService(
            FoundItemRepository items,
            TrackingCodeGenerator trackingCodes,
            ObjectStorage storage,
            ApplicationEventPublisher events,
            Clock clock) {
        this.items = items;
        this.trackingCodes = trackingCodes;
        this.storage = storage;
        this.events = events;
        this.clock = clock;
    }

    @Transactional
    public FoundItem logItem(
            String description,
            ItemCategory category,
            String zoneName,
            MapPoint point,
            String foundBy,
            MultipartFile photo) {
        FoundItem item = FoundItem.log(
                trackingCodes.next(), description, category, zoneName, point, null, foundBy, Instant.now(clock));
        if (photo != null && !photo.isEmpty()) {
            item.attachPhoto(storePhoto("items/" + item.id(), photo));
        }
        return publishAndSave(item);
    }

    @Transactional
    public FoundItem moveToStorage(UUID id, String location) {
        FoundItem item = requireItem(id);
        item.moveToStorage(location);
        return publishAndSave(item);
    }

    @Transactional
    public FoundItem openClaim(UUID id) {
        FoundItem item = requireItem(id);
        item.openClaim();
        return publishAndSave(item);
    }

    @Transactional
    public FoundItem reclaim(UUID id) {
        FoundItem item = requireItem(id);
        item.reclaim();
        return publishAndSave(item);
    }

    @Transactional
    public FoundItem markUnclaimed(UUID id) {
        FoundItem item = requireItem(id);
        item.markUnclaimed();
        return publishAndSave(item);
    }

    @Transactional
    public FoundItem dispose(UUID id) {
        FoundItem item = requireItem(id);
        item.dispose();
        return publishAndSave(item);
    }

    @Transactional(readOnly = true)
    public List<FoundItem> list() {
        return items.findAll();
    }

    @Transactional(readOnly = true)
    public FoundItem get(UUID id) {
        return requireItem(id);
    }

    @Transactional(readOnly = true)
    public ObjectStorage.StoredObject photo(UUID id) {
        FoundItem item = requireItem(id);
        String key = item.photoObjectKey().orElseThrow(() -> new ItemLifecycleException("Item has no photo"));
        return storage.load(key);
    }

    private FoundItem publishAndSave(FoundItem item) {
        var pending = item.pullEvents();
        FoundItem saved = items.save(item);
        pending.forEach(events::publishEvent);
        return saved;
    }

    private FoundItem requireItem(UUID id) {
        return items.findById(id).orElseThrow(() -> new ItemLifecycleException("Item not found: " + id));
    }

    private String storePhoto(String prefix, MultipartFile photo) {
        String original = photo.getOriginalFilename() == null ? "photo" : photo.getOriginalFilename();
        String key = prefix + "/" + original.replaceAll("[^a-zA-Z0-9._-]", "_");
        try (InputStream in = photo.getInputStream()) {
            return storage.store(key, photo.getContentType() == null ? "application/octet-stream" : photo.getContentType(), in, photo.getSize());
        } catch (IOException ex) {
            throw new IllegalStateException("Could not read uploaded photo", ex);
        }
    }
}
