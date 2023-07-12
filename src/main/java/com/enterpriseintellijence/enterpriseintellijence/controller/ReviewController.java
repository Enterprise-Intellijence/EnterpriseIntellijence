package com.enterpriseintellijence.enterpriseintellijence.controller;

import com.enterpriseintellijence.enterpriseintellijence.dto.ReviewDTO;
import com.enterpriseintellijence.enterpriseintellijence.data.services.ReviewService;
import com.enterpriseintellijence.enterpriseintellijence.dto.creation.ReviewCreateDTO;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.enterpriseintellijence.enterpriseintellijence.security.AppSecurityConfig.SECURITY_CONFIG_NAME;

@RestController
@RequiredArgsConstructor
@RequestMapping(path="/api/v1/reviews", produces="application/json")
@SecurityRequirement(name = SECURITY_CONFIG_NAME)
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping(consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public ReviewDTO createReview(@Valid @RequestBody ReviewCreateDTO reviewDTO) throws IllegalAccessException {
        return reviewService.createReview(reviewDTO);
    }

    /*
    @PutMapping(path = "/{id}",consumes="application/json")
    public ResponseEntity<ReviewDTO> replaceReview(@PathVariable("id") String id, @Valid @RequestBody ReviewDTO reviewDTO) throws IllegalAccessException {
        return ResponseEntity.ok(reviewService.replaceReview(id,reviewDTO));
    }

     */

    @PutMapping(path="/{id}", consumes = "application/json")
    public ResponseEntity<ReviewDTO> updateReview(@PathVariable("id") String id, @Valid @RequestBody ReviewDTO patch) throws IllegalAccessException {
        return ResponseEntity.ok(reviewService.updateReview(id,patch));
    }

    @DeleteMapping(path="/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteReview(@PathVariable("id") String id) throws IllegalAccessException {
        reviewService.deleteReview(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReviewDTO> reviewById(@PathVariable("id") String id){
        return ResponseEntity.ok(reviewService.reviewById(id));
    }

    @GetMapping("/{userId}/received")
    public Page<ReviewDTO> allReviewReceived(
            @PathVariable("userId") String userID,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int sizePage)
    {
    return reviewService.allReviewReceived(userID, page,sizePage);
    }

    @GetMapping("/{userId}/sent")
    public Page<ReviewDTO> allReviewSent(
            @PathVariable("userId") String userID,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int sizePage)
    {
        return reviewService.allReviewSent(userID, page,sizePage);
    }
}
