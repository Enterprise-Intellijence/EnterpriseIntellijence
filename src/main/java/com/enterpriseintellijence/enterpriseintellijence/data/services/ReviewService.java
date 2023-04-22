package com.enterpriseintellijence.enterpriseintellijence.data.services;

import com.enterpriseintellijence.enterpriseintellijence.dto.ReviewDTO;

public interface ReviewService {
    ReviewDTO createReview(ReviewDTO reviewDTO);

    ReviewDTO replaceReview(String id, ReviewDTO reviewDTO);

    ReviewDTO updateReview(String id, ReviewDTO reviewDTO);

    void deleteReview(String id);
    ReviewDTO reviewById(String id);

    Iterable<ReviewDTO> findAll();
}
