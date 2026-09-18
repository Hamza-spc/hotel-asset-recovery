package com.hotel.lostfound.inventory;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FoundItemRepository {

    FoundItem save(FoundItem item);

    Optional<FoundItem> findById(UUID id);

    List<FoundItem> findAll();
}
