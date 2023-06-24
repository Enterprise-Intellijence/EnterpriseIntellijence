package com.enterpriseintellijence.enterpriseintellijence.data.services;

import com.enterpriseintellijence.enterpriseintellijence.core.services.ProcessSaleServiceImp;
import com.enterpriseintellijence.enterpriseintellijence.data.entities.Order;
import com.enterpriseintellijence.enterpriseintellijence.data.entities.Product;
import com.enterpriseintellijence.enterpriseintellijence.data.entities.Review;
import com.enterpriseintellijence.enterpriseintellijence.data.entities.User;
import com.enterpriseintellijence.enterpriseintellijence.data.repository.OrderRepository;
import com.enterpriseintellijence.enterpriseintellijence.data.repository.ProductRepository;
import com.enterpriseintellijence.enterpriseintellijence.data.repository.ReviewRepository;
import com.enterpriseintellijence.enterpriseintellijence.data.repository.UserRepository;
import com.enterpriseintellijence.enterpriseintellijence.dto.ReviewDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.UserDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.creation.ReviewCreateDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.OrderState;
import com.enterpriseintellijence.enterpriseintellijence.exception.IdMismatchException;

import com.enterpriseintellijence.enterpriseintellijence.security.JwtContextUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewServiceImp implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final OrderRepository orderRepository;
    private final JwtContextUtils jwtContextUtils;
    private final ProcessSaleServiceImp processSaleServiceImp;


    private final ModelMapper modelMapper;

    private final UserService userService;
    private final UserRepository userRepository;

    @Override
    public ReviewDTO createReview(ReviewCreateDTO reviewDTO) throws IllegalAccessException {
        // TODO: 29/05/2023 rivedere con calma
       /* if (!checkOwnership(reviewDTO)) {
            throw new IllegalAccessException("User cannot review himself");
        }*/
        Order order = orderRepository.findById(reviewDTO.getOrderBasicDTO().getId()).orElseThrow(EntityNotFoundException::new);
        User loggedUser = jwtContextUtils.getUserLoggedFromContext();

        if(order.getProduct().getSeller().getId().equals(loggedUser.getId()))
            throw new IllegalAccessException("User cannot review himself");

        if(!order.getState().equals(OrderState.COMPLETED))
            throw new IllegalAccessException("Cannot review if order isn't completed");

        if (order.getOrderDate().equals(OrderState.REVIEWED))
            throw new IllegalAccessException("Order already reviewed");

        Review review = modelMapper.map(reviewDTO,Review.class);


        review.setReviewer(order.getProduct().getSeller());
        review.setDate(LocalDateTime.now());
        review.setReviewer(loggedUser);
        // TODO: 29/05/2023 controllare la relazione con gli user
        loggedUser.addReview(review.getVote());
        order.setState(OrderState.REVIEWED);

        review = reviewRepository.save(review);
        userRepository.save(loggedUser);
        orderRepository.save(order);
        return mapToDTO(review);
    }

    @Override
    public ReviewDTO replaceReview(String id, ReviewDTO reviewDTO) throws IllegalAccessException {

        throwOnIdMismatch(id, reviewDTO);

        Review oldReview = reviewRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        Review newReview = mapToEntity(reviewDTO);

        User requestingUser = jwtContextUtils.getUserLoggedFromContext();

        if(!requestingUser.getId().equals(oldReview.getReviewer().getId())) {
            throw new IllegalAccessException("User cannot change review");
        }
        if(!requestingUser.getId().equals(newReview.getReviewer().getId())) {
            throw new IllegalAccessException("User cannot change review");
        }
        requestingUser.editReview(oldReview.getVote(), newReview.getVote());
        newReview = reviewRepository.save(newReview);
        userRepository.save(requestingUser);
        return mapToDTO(newReview);
    }

    @Override
    public ReviewDTO updateReview(String id, ReviewDTO patch) throws IllegalAccessException {
        throwOnIdMismatch(id, patch);
        Review review = reviewRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        User user = jwtContextUtils.getUserLoggedFromContext();
        if(!review.getReviewer().getId().equals(user.getId())) {
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
            user.editReview(review.getVote(), patch.getVote());
        }

        reviewRepository.save(review);
        userRepository.save(user);
        return mapToDTO(review);
    }

    @Override
    public void deleteReview(String id) throws IllegalAccessException {
        Review review = reviewRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        User user = jwtContextUtils.getUserLoggedFromContext();
        if(!review.getReviewer().getId().equals(user.getId())) {
            throw new IllegalAccessException("User cannot delete review");
        }
        reviewRepository.deleteById(id);
        user.removeReview(review.getVote());
        userRepository.save(user);

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

    @Override
    public Page<ReviewDTO> allReviewReceived(String userId, int page, int sizePage) {
        //User user = jwtContextUtils.getUserLoggedFromContext();
        User user = userRepository.findById(userId).orElseThrow(EntityNotFoundException::new);
        if(user.getReceivedReviews()!=null)
            return new PageImpl<>(user.getReceivedReviews().stream().map(s->modelMapper.map(s,ReviewDTO.class)).collect(Collectors.toList()),PageRequest.of(page,sizePage),user.getReceivedReviews().size());
        else
            return new PageImpl<>(null,PageRequest.of(page,sizePage),0);
    }

    @Override
    public Page<ReviewDTO> allReviewSent(String userId, int page, int sizePage) {
        User user = userRepository.findById(userId).orElseThrow(EntityNotFoundException::new);
        if(user.getSentReviews()!=null)
            return new PageImpl<>(user.getSentReviews().stream().map(s->modelMapper.map(s,ReviewDTO.class)).collect(Collectors.toList()),PageRequest.of(page,sizePage),user.getSentReviews().size());
        else
            return new PageImpl<>(null,PageRequest.of(page,sizePage),0);

    }

    // TODO: VA TESTATA ASSOLUTAMENTE
    private Iterable<ReviewDTO> mapToDTO(Iterable<Review> reviews) {
        Iterable<ReviewDTO> reviewDTOs = new ArrayList<>();
        modelMapper.map(reviews,reviewDTOs);
        return reviewDTOs;
    }

/*    private boolean checkOwnership(ReviewDTO review){
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
    }*/

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
