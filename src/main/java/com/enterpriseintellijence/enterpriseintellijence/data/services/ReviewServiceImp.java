package com.enterpriseintellijence.enterpriseintellijence.data.services;

import com.enterpriseintellijence.enterpriseintellijence.data.entities.Product;
import com.enterpriseintellijence.enterpriseintellijence.data.entities.Review;
import com.enterpriseintellijence.enterpriseintellijence.data.repository.ReviewRepository;
import com.enterpriseintellijence.enterpriseintellijence.dto.ProductDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.ReviewDTO;
import com.enterpriseintellijence.enterpriseintellijence.exception.IdMismatchException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.hibernate.ObjectNotFoundException;
import org.hibernate.annotations.NotFound;
import org.modelmapper.ModelMapper;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.ArrayList;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReviewServiceImp implements ReviewService{

    private final ReviewRepository reviewRepository;

    private final ModelMapper modelMapper;


    public ReviewDTO createReview(ReviewDTO reviewDTO) {

        Review review = mapToEntity(reviewDTO);
        review = reviewRepository.save(review);

        return mapToDTO(review);
    }

    public ReviewDTO replaceReview(String id, ReviewDTO reviewDTO) {

        throwOnIdMismatch(id, reviewDTO);
        Review review = mapToEntity(reviewDTO);
        review = reviewRepository.save(review);

        return mapToDTO(review);
    }

    public ReviewDTO updateReview(String id, ReviewDTO reviewDTO) {
        throwOnIdMismatch(id, reviewDTO);
        Review review = mapToEntity(reviewDTO);

        // TODO: 21/04/2023

        return null;
    }

    public void deleteReview(String id) {
        Optional<Review> review = reviewRepository.findById(id);

        if(!review.isPresent()) {
            throw new EntityNotFoundException("Review not found");
        }
        reviewRepository.deleteById(id);
    }

    public ReviewDTO reviewById(String id) {
        Optional<Review> review = reviewRepository.findById(id);

        if(!review.isPresent()) {
            throw new EntityNotFoundException("Review not found");
        }
        return mapToDTO(review.get());
    }

    public Iterable<ReviewDTO> findAll() {
        Iterable<Review> reviews = reviewRepository.findAll();
        return mapToDTO(reviews);
    }

    // TODO: VA TESTATA ASSOLUTAMENTE
    private Iterable<ReviewDTO> mapToDTO(Iterable<Review> reviews) {
        Iterable<ReviewDTO> reviewDTOs = new ArrayList<>();
        modelMapper.map(reviews,reviewDTOs);
        return reviewDTOs;
    }

    private Review mapToEntity(ReviewDTO reviewDTO) {
        return modelMapper.map(reviewDTO,Review.class);
    }

    private ReviewDTO mapToDTO(Review review) {
        return modelMapper.map(review,ReviewDTO.class);
    }

    private void throwOnIdMismatch(String id, ReviewDTO reviewDTO) {
        if (reviewDTO.getId() != null && !reviewDTO.getId().equals(id)) {
            throw new IdMismatchException();
        }
    }
}
