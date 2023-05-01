package com.enterpriseintellijence.enterpriseintellijence.data.services;

import com.enterpriseintellijence.enterpriseintellijence.dto.ReviewDTO;

public interface ReviewService {
    ReviewDTO createReview(ReviewDTO reviewDTO);

    ReviewDTO replaceReview(String id, ReviewDTO reviewDTO) throws IllegalAccessException;

    ReviewDTO updateReview(String id, ReviewDTO patch);

    ReviewDTO deleteReview(String id);
    ReviewDTO reviewById(String id);

    Iterable<ReviewDTO> findAll();
}
