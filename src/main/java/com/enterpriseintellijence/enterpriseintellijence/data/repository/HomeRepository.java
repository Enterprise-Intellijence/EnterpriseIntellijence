package com.enterpriseintellijence.enterpriseintellijence.data.repository;

import com.enterpriseintellijence.enterpriseintellijence.data.entities.Clothing;
import com.enterpriseintellijence.enterpriseintellijence.data.entities.Entertainment;
import com.enterpriseintellijence.enterpriseintellijence.data.entities.Home;
import com.enterpriseintellijence.enterpriseintellijence.data.entities.Product;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.EntertainmentType;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.HomeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface HomeRepository  extends JpaRepository<Home,String>, JpaSpecificationExecutor<Clothing> {
    Page<Product> findAllByHomeType(HomeType homeType, Pageable pageable);
}
