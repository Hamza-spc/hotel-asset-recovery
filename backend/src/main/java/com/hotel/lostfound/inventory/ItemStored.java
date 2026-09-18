package com.hotel.lostfound.inventory;

import java.time.Instant;
import java.util.UUID;

public record ItemStored(UUID itemId, String storageLocation, Instant occurredAt) {}
