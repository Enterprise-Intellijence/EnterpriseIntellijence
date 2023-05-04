package com.enterpriseintellijence.enterpriseintellijence.data.repository;

import com.enterpriseintellijence.enterpriseintellijence.data.entities.Delivery;
import com.enterpriseintellijence.enterpriseintellijence.dto.DeliveryDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface DeliveryRepository extends JpaRepository<Delivery, String>, JpaSpecificationExecutor<Delivery> {
}
