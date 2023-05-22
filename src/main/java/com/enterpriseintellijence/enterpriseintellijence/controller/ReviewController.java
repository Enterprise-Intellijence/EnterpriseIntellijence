package com.enterpriseintellijence.enterpriseintellijence.controller;

import com.enterpriseintellijence.enterpriseintellijence.dto.ReviewDTO;
import com.enterpriseintellijence.enterpriseintellijence.data.services.ReviewService;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@RestController
@RequiredArgsConstructor
@RequestMapping(path="/api/v1/reviews", produces="application/json")
public class ReviewController {
    private final ReviewService reviewService;

    private final Bandwidth limit = Bandwidth.classic(20, Refill.greedy(25, Duration.ofMinutes(1)));
    private final Bucket bucket = Bucket.builder().addLimit(limit).build();

    @PostMapping(consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public ReviewDTO createReview(@Valid @RequestBody ReviewDTO reviewDTO) throws IllegalAccessException {
        return reviewService.createReview(reviewDTO);
    }

    @PutMapping(path = "/{id}",consumes="application/json")
    public ResponseEntity<ReviewDTO> replaceReview(@PathVariable("id") String id, @Valid @RequestBody ReviewDTO reviewDTO) throws IllegalAccessException {
        return ResponseEntity.ok(reviewService.replaceReview(id,reviewDTO));
    }

    @PatchMapping(path="/{id}", consumes = "application/json")
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

    // TODO: avrebbe più senso fare un metodo allReviewsByUser, che restituisce tutte le recensioni riferite ad uno stesso utente
    // forse è meglio parlarne quando si ha il frontend
    @GetMapping("")
    public Iterable<ReviewDTO> allReview() {
        return reviewService.findAll();
    }
}
