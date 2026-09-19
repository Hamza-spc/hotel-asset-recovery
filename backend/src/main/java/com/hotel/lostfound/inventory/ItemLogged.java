package com.hotel.lostfound.inventory;

import java.time.Instant;
import java.util.UUID;
import org.springframework.modulith.events.Externalized;

@Externalized("lostfound.inventory::#{#this.itemId()}")
public record ItemLogged(UUID itemId, String trackingCode, Instant occurredAt) {}
