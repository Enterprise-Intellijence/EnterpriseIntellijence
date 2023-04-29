package com.enterpriseintellijence.enterpriseintellijence.data.services;

import com.enterpriseintellijence.enterpriseintellijence.data.entities.Review;
import com.enterpriseintellijence.enterpriseintellijence.data.entities.User;
import com.enterpriseintellijence.enterpriseintellijence.data.repository.ReviewRepository;
import com.enterpriseintellijence.enterpriseintellijence.dto.OfferDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.ReviewDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.UserDTO;
import com.enterpriseintellijence.enterpriseintellijence.exception.IdMismatchException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReviewServiceImp implements ReviewService {

    private final ReviewRepository reviewRepository;

    private final ModelMapper modelMapper;

    private final UserService userService;


    @Override
    public ReviewDTO createReview(ReviewDTO reviewDTO) {
        Review review = mapToEntity(reviewDTO);

        UserDTO userDTO = userService.findUserFromContext()
                .orElseThrow(EntityNotFoundException::new);

        review.setReviewer(modelMapper.map(userDTO, User.class));

        review = reviewRepository.save(review);

        return mapToDTO(review);
    }

    @Override
    public ReviewDTO replaceReview(String id, ReviewDTO reviewDTO) throws IllegalAccessException {

        throwOnIdMismatch(id, reviewDTO);

        Review oldReview = reviewRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        Review newReview = mapToEntity(reviewDTO);

        UserDTO requestingUser = userService.findUserFromContext()
                .orElseThrow(EntityNotFoundException::new);

        if(!requestingUser.getId().equals(oldReview.getReviewer().getId())) {
            throw new IllegalAccessException("User cannot change review");
        }
        if(!requestingUser.getId().equals(newReview.getReviewer().getId())) {
            throw new IllegalAccessException("User cannot change review");
        }

        newReview = reviewRepository.save(newReview);

        return mapToDTO(newReview);
    }

    @Override
    public ReviewDTO updateReview(String id, JsonPatch patch) throws JsonPatchException {
        ReviewDTO review = mapToDTO(reviewRepository.findById(id).orElseThrow(EntityNotFoundException::new));
        review = applyPatch(patch, mapToEntity(review));
        reviewRepository.save(mapToEntity(review));
        return review;
    }

    @Override
    public ReviewDTO deleteReview(String id) {
        Review review = reviewRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        reviewRepository.deleteById(id);

        return mapToDTO(review);
    }

    @Override
    public ReviewDTO reviewById(String id) {
        Review review = reviewRepository.findById(id).orElseThrow(EntityNotFoundException::new);

        return mapToDTO(review);
    }

    @Override
    public Iterable<ReviewDTO> findAll() {
        Iterable<Review> reviews = reviewRepository.findAll();
        return mapToDTO(reviews);
    }

    public ReviewDTO applyPatch(JsonPatch patch, Review review) throws JsonPatchException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode patched = patch.apply(objectMapper.convertValue(review, JsonNode.class));

        return objectMapper.convertValue(patched, ReviewDTO.class);
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
