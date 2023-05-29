package com.enterpriseintellijence.enterpriseintellijence.data.services;

import com.enterpriseintellijence.enterpriseintellijence.dto.ReviewDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.creation.ReviewCreateDTO;

public interface ReviewService {
    ReviewDTO createReview(ReviewCreateDTO reviewDTO) throws IllegalAccessException;

    ReviewDTO replaceReview(String id, ReviewDTO reviewDTO) throws IllegalAccessException;

    ReviewDTO updateReview(String id, ReviewDTO patch) throws IllegalAccessException;

    void deleteReview(String id) throws IllegalAccessException;
    ReviewDTO reviewById(String id);

    Iterable<ReviewDTO> findAll();
}
