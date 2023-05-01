package com.enterpriseintellijence.enterpriseintellijence.controller;

import com.enterpriseintellijence.enterpriseintellijence.data.repository.ProductRepository;
import com.enterpriseintellijence.enterpriseintellijence.data.services.PaymentMethodService;
import com.enterpriseintellijence.enterpriseintellijence.dto.PaymentMethodDTO;

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
@RequestMapping("/api/v1/payment-methods")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class PaymentMethodController {

    private final PaymentMethodService paymentMethodService;

    private final ProductRepository productRepository;

    private final Bandwidth limit = Bandwidth.classic(20, Refill.greedy(25, Duration.ofMinutes(1)));
    private final Bucket bucket = Bucket.builder().addLimit(limit).build();

    @PostMapping(consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public PaymentMethodDTO createPaymentMethod(@Valid @RequestBody PaymentMethodDTO paymentMethodDTO) {productRepository.findAll();
        return paymentMethodService.createPaymentMethod(paymentMethodDTO);
    }

    @PutMapping(path = "/{id}", consumes = "application/json")
    public PaymentMethodDTO replacePaymentMethod(@PathVariable("id") String id, @Valid @RequestBody PaymentMethodDTO paymentMethodDTO) {
        return paymentMethodService.replacePaymentMethod(id, paymentMethodDTO).getBody();
    }

    @PatchMapping(path = "/{id}", consumes = "application/json")
    public ResponseEntity<PaymentMethodDTO> updatePaymentMethod(@PathVariable("id") String id, @Valid @RequestBody PaymentMethodDTO paymentMethodDTO) {
        return paymentMethodService.updatePaymentMethod(id, paymentMethodDTO);
    }

    @DeleteMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<PaymentMethodDTO> deletePaymentMethod(@PathVariable("id") String id) {
        return paymentMethodService.deletePaymentMethod(id);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<PaymentMethodDTO> getPaymentMethod(@PathVariable("id") String id) {
        return paymentMethodService.getPaymentMethod(id);
    }
}
