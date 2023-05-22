package com.enterpriseintellijence.enterpriseintellijence.data.services;

import com.enterpriseintellijence.enterpriseintellijence.data.entities.Review;
import com.enterpriseintellijence.enterpriseintellijence.data.entities.User;
import com.enterpriseintellijence.enterpriseintellijence.data.repository.ReviewRepository;
import com.enterpriseintellijence.enterpriseintellijence.dto.ReviewDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.UserDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.OrderState;
import com.enterpriseintellijence.enterpriseintellijence.exception.IdMismatchException;

import com.enterpriseintellijence.enterpriseintellijence.security.JwtContextUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@RequiredArgsConstructor
public class ReviewServiceImp implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final OrderService orderService;
    private final JwtContextUtils jwtContextUtils;

    private final ModelMapper modelMapper;

    private final UserService userService;


    @Override
    public ReviewDTO createReview(ReviewDTO reviewDTO) throws IllegalAccessException {

        if (!checkOwnership(reviewDTO)) {
            throw new IllegalAccessException("User cannot review himself");
        }
        Review review = mapToEntity(reviewDTO);

        UserDTO userDTO = userService.findUserFromContext()
                .orElseThrow(EntityNotFoundException::new);

        review.setReviewer(modelMapper.map(userDTO, User.class));
        review.setDate(LocalDateTime.now());

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
    public ReviewDTO updateReview(String id, ReviewDTO patch) throws IllegalAccessException {
        throwOnIdMismatch(id, patch);
        Review review = reviewRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        if(!review.getReviewer().getId().equals(jwtContextUtils.getUserLoggedFromContext().getId())) {
            throw new IllegalAccessException("User cannot change review");
        }

        if (patch.getTitle() != null) {
            review.setTitle(patch.getTitle());
        }

        if (patch.getDescription() != null) {
            review.setDescription(patch.getDescription());
        }

        if (patch.getVote() != null) {
            review.setVote(patch.getVote());
        }

        reviewRepository.save(review);
        return mapToDTO(review);
    }

    @Override
    public void deleteReview(String id) throws IllegalAccessException {
        Review review = reviewRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        if(!review.getReviewer().getId().equals(jwtContextUtils.getUserLoggedFromContext().getId())) {
            throw new IllegalAccessException("User cannot delete review");
        }
        reviewRepository.deleteById(id);

        mapToDTO(review);
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

    // TODO: VA TESTATA ASSOLUTAMENTE
    private Iterable<ReviewDTO> mapToDTO(Iterable<Review> reviews) {
        Iterable<ReviewDTO> reviewDTOs = new ArrayList<>();
        modelMapper.map(reviews,reviewDTOs);
        return reviewDTOs;
    }

    private boolean checkOwnership(ReviewDTO review){
        AtomicBoolean result = new AtomicBoolean(false);
        UserDTO userDTO = userService.findUserFromContext()
                .orElseThrow(EntityNotFoundException::new);
        orderService.findAllByUserId(userDTO.getId(), Pageable.unpaged()).forEach(orderDTO -> {
            if(orderDTO.getUser().getId().equals(review.getReviewer().getId())
                    && orderDTO.getState().equals(OrderState.COMPLETED)){
                result.set(true);
            }
        });
        return result.get();
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
