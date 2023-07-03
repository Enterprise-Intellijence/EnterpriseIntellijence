package com.enterpriseintellijence.enterpriseintellijence.controller;

import com.enterpriseintellijence.enterpriseintellijence.data.repository.ProductRepository;
import com.enterpriseintellijence.enterpriseintellijence.data.services.PaymentMethodService;
import com.enterpriseintellijence.enterpriseintellijence.dto.PaymentMethodDTO;

import com.enterpriseintellijence.enterpriseintellijence.dto.basics.PaymentMethodBasicDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.creation.PaymentMethodCreateDTO;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

import static com.enterpriseintellijence.enterpriseintellijence.security.AppSecurityConfig.SECURITY_CONFIG_NAME;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payment-methods")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@SecurityRequirement(name = SECURITY_CONFIG_NAME)
public class PaymentMethodController {
    // TODO: 16/05/23 Erne 

    private final PaymentMethodService paymentMethodService;

    private final ProductRepository productRepository;

    @PostMapping(consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<PaymentMethodDTO> createPaymentMethod(@Valid @RequestBody PaymentMethodCreateDTO paymentMethodCreateDTO) throws IllegalAccessException {
        productRepository.findAll();
        return ResponseEntity.ok(paymentMethodService.createPaymentMethod(paymentMethodCreateDTO));
    }

    @PutMapping(path = "/{id}", consumes = "application/json")
    public ResponseEntity<PaymentMethodDTO> replacePaymentMethod(@PathVariable("id") String id, @Valid @RequestBody PaymentMethodDTO paymentMethodDTO) throws IllegalAccessException {
        return ResponseEntity.ok(paymentMethodService.replacePaymentMethod(id, paymentMethodDTO));
    }

    @PatchMapping(path = "/{id}", consumes = "application/json")
    public ResponseEntity<PaymentMethodDTO> updatePaymentMethod(@PathVariable("id") String id, @Valid @RequestBody PaymentMethodDTO paymentMethodDTO) throws IllegalAccessException {
        return ResponseEntity.ok(paymentMethodService.updatePaymentMethod(id, paymentMethodDTO));
    }

    @DeleteMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deletePaymentMethod(@PathVariable("id") String id) throws IllegalAccessException {
        paymentMethodService.deletePaymentMethod(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<PaymentMethodDTO> getPaymentMethod(@PathVariable("id") String id) throws IllegalAccessException {
        return ResponseEntity.ok(paymentMethodService.getPaymentMethodById(id));
    }

    @GetMapping()
    public ResponseEntity<Page<PaymentMethodBasicDTO>> getMyPaymentMethods(@RequestParam int page, @RequestParam int size) throws EntityNotFoundException {
        return ResponseEntity.ok(paymentMethodService.getMyPaymentMethods(page, size));
    }
}
