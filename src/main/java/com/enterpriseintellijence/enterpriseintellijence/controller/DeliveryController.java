package com.enterpriseintellijence.enterpriseintellijence.controller;

import com.enterpriseintellijence.enterpriseintellijence.data.entities.Address;
import com.enterpriseintellijence.enterpriseintellijence.data.services.DeliveryService;
import com.enterpriseintellijence.enterpriseintellijence.dto.AddressDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.DeliveryDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.creation.AddressCreateDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.creation.DeliveryCreateDTO;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.List;

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
    public ResponseEntity<DeliveryDTO> createDelivery(@Valid @RequestBody DeliveryCreateDTO deliveryDTO) throws IllegalAccessException {
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
    public void deleteDelivery(@PathVariable("id") String id) throws IllegalAccessException {
        deliveryService.deleteDelivery(id);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<DeliveryDTO> getDelivery(@PathVariable("id") String id) throws IllegalAccessException {
        return ResponseEntity.ok(deliveryService.getDeliveryById(id));
    }

    @PostMapping(path = "/address",consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<AddressDTO> createAddress(@Valid @RequestBody AddressCreateDTO addressCreateDTO) throws IllegalAccessException {
        return ResponseEntity.ok(deliveryService.createAddress(addressCreateDTO));
    }

    @PutMapping(path = "/address/{id}", consumes = "application/json")
    public ResponseEntity<AddressDTO> replaceAddress(@PathVariable("id") String id, @Valid @RequestBody AddressDTO addressDTO) throws IllegalAccessException {
        return ResponseEntity.ok(deliveryService.replaceAddress(id, addressDTO));
    }

    @PatchMapping(path = "/address/{id}", consumes = "application/json")
    public ResponseEntity<AddressDTO> updateAddress(@PathVariable("id") String id, @Valid @RequestBody AddressDTO addressDTO) throws IllegalAccessException {
        return ResponseEntity.ok(deliveryService.updateAddress(id, addressDTO));
    }

    @DeleteMapping(path = "/address/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAddress(@PathVariable("id") String id) throws IllegalAccessException {
        deliveryService.deleteAddress(id);
    }

    //todo solo questo pubblico
    @GetMapping(path = "/address/{id}")
    public ResponseEntity<AddressDTO> getAddress(@PathVariable("id") String id) throws IllegalAccessException {
        return ResponseEntity.ok(deliveryService.getAddress(id));
    }

    @GetMapping(path = "/address")
    public ResponseEntity<Iterable<AddressDTO>> getMyAddressList() throws IllegalAccessException {
        return ResponseEntity.ok(deliveryService.getMyAddressList());
    }

    @PostMapping(path = "/address/set/default/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<AddressDTO> changeDefaultAddress(@PathVariable("id") String id) throws IllegalAccessException {
        return ResponseEntity.ok(deliveryService.changeDefaultAddress(id));
    }


}
