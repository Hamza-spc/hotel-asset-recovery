package com.hotel.lostfound.inventory.persistence;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

interface FoundItemJpaRepository extends JpaRepository<FoundItemEntity, UUID> {}
