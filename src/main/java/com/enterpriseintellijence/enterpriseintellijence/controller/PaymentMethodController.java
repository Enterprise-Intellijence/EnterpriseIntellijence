package com.enterpriseintellijence.enterpriseintellijence.controller;

import com.enterpriseintellijence.enterpriseintellijence.data.services.PaymentMethodService;
import com.enterpriseintellijence.enterpriseintellijence.dto.PaymentMethodDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payment-methods")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class PaymentMethodController {

    private final PaymentMethodService paymentMethodService;

    @PostMapping(consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public PaymentMethodDTO createPaymentMethod(@RequestBody PaymentMethodDTO paymentMethodDTO) {
        return paymentMethodService.createPaymentMethod(paymentMethodDTO);
    }

    @PutMapping(path = "/{id}", consumes = "application/json")
    public PaymentMethodDTO replacePaymentMethod(@PathVariable("id") String id, @RequestBody PaymentMethodDTO paymentMethodDTO) {
        return paymentMethodService.replacePaymentMethod(paymentMethodDTO).getBody();
    }

    @PatchMapping(path = "/{id}", consumes = "application/json")
    public ResponseEntity<PaymentMethodDTO> updatePaymentMethod(@PathVariable("id") String id, @RequestBody PaymentMethodDTO paymentMethodDTO) {
        return paymentMethodService.updatePaymentMethod(paymentMethodDTO);
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
