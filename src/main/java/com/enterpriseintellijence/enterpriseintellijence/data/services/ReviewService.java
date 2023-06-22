package com.enterpriseintellijence.enterpriseintellijence.data.services;

import com.enterpriseintellijence.enterpriseintellijence.dto.ReviewDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.creation.ReviewCreateDTO;
import org.springframework.data.domain.Page;

public interface ReviewService {
    ReviewDTO createReview(ReviewCreateDTO reviewDTO) throws IllegalAccessException;

    ReviewDTO replaceReview(String id, ReviewDTO reviewDTO) throws IllegalAccessException;

    ReviewDTO updateReview(String id, ReviewDTO patch) throws IllegalAccessException;

    void deleteReview(String id) throws IllegalAccessException;
    ReviewDTO reviewById(String id);

    Iterable<ReviewDTO> findAll();




    Page<ReviewDTO> allReviewSent(String userId, int page, int sizePage);

    Page<ReviewDTO> allReviewReceived(String userId, int page, int sizePage);
}
