package com.hotel.lostfound.inventory;

import java.time.Instant;
import java.util.UUID;
import org.springframework.modulith.events.Externalized;

@Externalized("lostfound.inventory::#{#this.itemId()}")
public record ItemUnclaimed(UUID itemId, Instant occurredAt) {}
