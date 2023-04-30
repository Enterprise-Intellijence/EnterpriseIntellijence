package com.enterpriseintellijence.enterpriseintellijence.data.repository;

import com.enterpriseintellijence.enterpriseintellijence.data.entities.Order;
import com.enterpriseintellijence.enterpriseintellijence.dto.OrderDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface OrderRepository extends JpaRepository<Order, String>, JpaSpecificationExecutor<Order> {
    Page<Order> findAllByUserId(String userId, Pageable pageable);
}
