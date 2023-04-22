package com.enterpriseintellijence.enterpriseintellijence.controller;

import com.enterpriseintellijence.enterpriseintellijence.dto.ReviewDTO;
import com.enterpriseintellijence.enterpriseintellijence.data.services.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(path="/api/v1/reviews", produces="application/json")
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping(consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public ReviewDTO createReview(@RequestBody ReviewDTO reviewDTO){
        return reviewService.createReview(reviewDTO);
    }

    @PutMapping(path = "/{id}",consumes="application/json")
    public ResponseEntity<ReviewDTO> replaceReview(@PathVariable("id") String id, @RequestBody ReviewDTO reviewDTO){
        return ResponseEntity.ok(reviewService.replaceReview(id,reviewDTO));
    }

    @PatchMapping(path="/{id}", consumes = "application/json")
    public ResponseEntity<ReviewDTO> updateReview(@PathVariable("id") String id, @RequestBody ReviewDTO reviewDTO) {
        return ResponseEntity.ok(reviewService.updateReview(id,reviewDTO));
    }

    @DeleteMapping(path="/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteReview(@PathVariable("id") String id){
        reviewService.deleteReview(id);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReviewDTO> reviewById(@PathVariable("id") String id){
        return ResponseEntity.ok(reviewService.reviewById(id));
    }

    @GetMapping("")
    public Iterable<ReviewDTO> allReview() {
        return reviewService.findAll();
    }
}
