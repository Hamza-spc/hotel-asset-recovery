package com.hotel.lostfound.claims.persistence;

import com.hotel.lostfound.claims.LossReport;
import com.hotel.lostfound.claims.LossReportRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

@Repository
class JpaLossReportRepository implements LossReportRepository {

    private final LossReportJpaRepository jpa;

    JpaLossReportRepository(LossReportJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public LossReport save(LossReport report) {
        LossReportEntity entity = new LossReportEntity(
                report.id(),
                report.guestName(),
                report.roomNumber(),
                report.contact(),
                report.description(),
                report.photoObjectKey().orElse(null),
                report.zoneName(),
                report.status(),
                report.filedBy(),
                report.filedAt(),
                report.version());
        return toDomain(jpa.save(entity));
    }

    @Override
    public Optional<LossReport> findById(UUID id) {
        return jpa.findById(id).map(JpaLossReportRepository::toDomain);
    }

    @Override
    public List<LossReport> findAll() {
        return jpa.findAll(Sort.by(Sort.Direction.DESC, "filedAt")).stream()
                .map(JpaLossReportRepository::toDomain)
                .toList();
    }

    private static LossReport toDomain(LossReportEntity entity) {
        return LossReport.rehydrate(
                entity.getId(),
                entity.getGuestName(),
                entity.getRoomNumber(),
                entity.getContact(),
                entity.getDescription(),
                entity.getPhotoObjectKey(),
                entity.getZoneName(),
                entity.getStatus(),
                entity.getFiledBy(),
                entity.getFiledAt(),
                entity.getVersion());
    }
}
