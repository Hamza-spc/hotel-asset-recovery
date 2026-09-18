package com.hotel.lostfound.inventory;

import java.time.Instant;
import java.util.UUID;

public record ItemReclaimed(UUID itemId, Instant occurredAt) {}
