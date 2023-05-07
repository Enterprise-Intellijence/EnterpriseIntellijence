package com.enterpriseintellijence.enterpriseintellijence.data.repository;

import com.enterpriseintellijence.enterpriseintellijence.data.entities.Clothing;
import com.enterpriseintellijence.enterpriseintellijence.data.entities.Entertainment;
import com.enterpriseintellijence.enterpriseintellijence.data.entities.Home;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface HomeRepository  extends JpaRepository<Home,String>, JpaSpecificationExecutor<Clothing> {
}
