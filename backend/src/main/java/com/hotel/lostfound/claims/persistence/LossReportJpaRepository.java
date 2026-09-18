package com.hotel.lostfound.claims.persistence;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

interface LossReportJpaRepository extends JpaRepository<LossReportEntity, UUID> {}
