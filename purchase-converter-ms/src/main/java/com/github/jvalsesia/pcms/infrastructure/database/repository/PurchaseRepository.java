package com.github.jvalsesia.pcms.infrastructure.database.repository;


import com.github.jvalsesia.pcms.infrastructure.database.entity.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, UUID> {
}
