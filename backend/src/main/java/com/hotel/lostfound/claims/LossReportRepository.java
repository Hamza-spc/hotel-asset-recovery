package com.hotel.lostfound.claims;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LossReportRepository {

    LossReport save(LossReport report);

    Optional<LossReport> findById(UUID id);

    List<LossReport> findAll();
}
