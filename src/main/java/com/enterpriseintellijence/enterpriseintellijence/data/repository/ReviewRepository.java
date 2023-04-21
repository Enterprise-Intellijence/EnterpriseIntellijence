package com.enterpriseintellijence.enterpriseintellijence.data.repository;

import com.enterpriseintellijence.enterpriseintellijence.data.entities.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review,String>, JpaSpecificationExecutor<Review> {
}
