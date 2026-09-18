package com.hotel.lostfound.claims;

import java.time.Instant;
import java.util.UUID;

public record LossReportFiled(UUID reportId, String guestName, Instant occurredAt) {}
