package com.hotel.lostfound.inventory;

import java.time.Instant;
import java.util.UUID;

public record ItemLogged(UUID itemId, String trackingCode, Instant occurredAt) {}
