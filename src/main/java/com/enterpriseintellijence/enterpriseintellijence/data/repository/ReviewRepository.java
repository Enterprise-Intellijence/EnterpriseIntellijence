package com.enterpriseintellijence.enterpriseintellijence.data.repository;

import com.enterpriseintellijence.enterpriseintellijence.data.entities.Review;
import com.enterpriseintellijence.enterpriseintellijence.data.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review,String>, JpaSpecificationExecutor<Review> {
    Page<Review> findAllByReviewerEquals(User user, Pageable pageable);
    Page<Review> findAllByReviewedEquals(User user, Pageable pageable);
}
