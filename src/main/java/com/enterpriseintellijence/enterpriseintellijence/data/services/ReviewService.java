package com.enterpriseintellijence.enterpriseintellijence.data.services;

import com.enterpriseintellijence.enterpriseintellijence.dto.ReviewDTO;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;

import java.util.List;

public interface ReviewService {
    ReviewDTO createReview(ReviewDTO reviewDTO);

    ReviewDTO replaceReview(String id, ReviewDTO reviewDTO) throws IllegalAccessException;

    ReviewDTO updateReview(String id, JsonPatch patch) throws JsonPatchException;

    ReviewDTO deleteReview(String id);
    ReviewDTO reviewById(String id);

    Iterable<ReviewDTO> findAll();
}
