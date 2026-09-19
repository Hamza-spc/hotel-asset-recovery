package com.hotel.lostfound.claims;

import java.util.List;
import java.util.UUID;

public interface ReportMatchingPort {

    List<ReportSnapshot> snapshots();

    void markMatched(UUID reportId);

    void resolveFromMatch(UUID reportId);

    void reopen(UUID reportId);
}
