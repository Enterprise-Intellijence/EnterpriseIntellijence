package com.enterpriseintellijence.enterpriseintellijence.controller;

import com.enterpriseintellijence.enterpriseintellijence.data.services.DeliveryService;
import com.enterpriseintellijence.enterpriseintellijence.dto.DeliveryDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/deliveries", produces = "application/json")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class DeliveryController {

    private final DeliveryService deliveryService;

    @PostMapping(consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public DeliveryDTO createDelivery(@RequestBody DeliveryDTO deliveryDTO) {
        return deliveryService.createDelivery(deliveryDTO);
    }

    @PutMapping(path = "/{id}", consumes = "application/json")
    public DeliveryDTO replaceDelivery(@PathVariable("id") String id, @RequestBody DeliveryDTO deliveryDTO) {
        return deliveryService.replaceDelivery(deliveryDTO).getBody();
    }

    @PatchMapping(path = "/{id}", consumes = "application/json")
    public ResponseEntity<DeliveryDTO> updateDelivery(@PathVariable("id") String id, @RequestBody DeliveryDTO deliveryDTO) {
        return deliveryService.updateDelivery(deliveryDTO);
    }

    @DeleteMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<DeliveryDTO> deleteDelivery(@PathVariable("id") String id) {
        return deliveryService.deleteDelivery(id);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<DeliveryDTO> getDelivery(@PathVariable("id") String id) {
        return deliveryService.getDelivery(id);
    }

    @GetMapping(path = "")
    public Iterable<DeliveryDTO> getAllDeliveries() {
        return deliveryService.getAllDeliveries();
    }
}
