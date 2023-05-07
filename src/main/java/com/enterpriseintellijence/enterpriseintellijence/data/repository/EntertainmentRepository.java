package com.enterpriseintellijence.enterpriseintellijence.data.repository;

import com.enterpriseintellijence.enterpriseintellijence.data.entities.Clothing;
import com.enterpriseintellijence.enterpriseintellijence.data.entities.Entertainment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface EntertainmentRepository extends JpaRepository<Entertainment,String>, JpaSpecificationExecutor<Clothing> {
}

