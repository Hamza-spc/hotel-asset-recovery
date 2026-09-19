package com.hotel.lostfound.claims;

import java.util.UUID;

public record ReportSnapshot(
        UUID id, String guestName, String description, Double mapX, Double mapY, LossReportStatus status) {

    public boolean availableForMatch() {
        return status == LossReportStatus.OPEN || status == LossReportStatus.MATCHED;
    }
}
