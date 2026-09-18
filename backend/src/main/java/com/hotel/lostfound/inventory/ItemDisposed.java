package com.hotel.lostfound.inventory;

import java.time.Instant;
import java.util.UUID;

public record ItemDisposed(UUID itemId, Instant occurredAt) {}
