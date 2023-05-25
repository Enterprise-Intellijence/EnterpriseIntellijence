package com.enterpriseintellijence.enterpriseintellijence.controller;

import com.enterpriseintellijence.enterpriseintellijence.data.services.DeliveryService;
import com.enterpriseintellijence.enterpriseintellijence.dto.DeliveryDTO;
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
@RequestMapping(value = "/api/v1/deliveries", produces = "application/json")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class DeliveryController {

    private final DeliveryService deliveryService;

    private final Bandwidth limit = Bandwidth.classic(20, Refill.greedy(25, Duration.ofMinutes(1)));
    private final Bucket bucket = Bucket.builder().addLimit(limit).build();

    @PostMapping(consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<DeliveryDTO> createDelivery(@Valid @RequestBody DeliveryDTO deliveryDTO) throws IllegalAccessException {
        return ResponseEntity.ok(deliveryService.createDelivery(deliveryDTO));
    }

    @PutMapping(path = "/{id}", consumes = "application/json")
    public ResponseEntity<DeliveryDTO> replaceDelivery(@PathVariable("id") String id, @Valid @RequestBody DeliveryDTO deliveryDTO) throws IllegalAccessException {
        return ResponseEntity.ok(deliveryService.replaceDelivery(id, deliveryDTO));
    }

    @PatchMapping(path = "/{id}", consumes = "application/json")
    public ResponseEntity<DeliveryDTO> updateDelivery(@PathVariable("id") String id, @Valid @RequestBody DeliveryDTO deliveryDTO) throws IllegalAccessException {
        return ResponseEntity.ok(deliveryService.updateDelivery(id, deliveryDTO));
    }

    @DeleteMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteDelivery(@PathVariable("id") String id) throws IllegalAccessException {
        deliveryService.deleteDelivery(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<DeliveryDTO> getDelivery(@PathVariable("id") String id) throws IllegalAccessException {
        return ResponseEntity.ok(deliveryService.getDeliveryById(id));
    }
}
