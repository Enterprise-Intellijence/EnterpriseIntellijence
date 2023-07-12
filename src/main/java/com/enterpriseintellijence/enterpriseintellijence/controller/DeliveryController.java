package com.enterpriseintellijence.enterpriseintellijence.controller;

import com.enterpriseintellijence.enterpriseintellijence.data.services.DeliveryService;
import com.enterpriseintellijence.enterpriseintellijence.dto.AddressDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.DeliveryDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.creation.AddressCreateDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.creation.DeliveryCreateDTO;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.enterpriseintellijence.enterpriseintellijence.security.AppSecurityConfig.SECURITY_CONFIG_NAME;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/deliveries", produces = "application/json")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@SecurityRequirement(name = SECURITY_CONFIG_NAME)
public class DeliveryController {

    private final DeliveryService deliveryService;


    @PostMapping(consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<DeliveryDTO> createDelivery(@Valid @RequestBody DeliveryCreateDTO deliveryDTO) throws IllegalAccessException {
        return ResponseEntity.ok(deliveryService.createDelivery(deliveryDTO));
    }

    @PutMapping(path = "/{id}", consumes = "application/json")
    public ResponseEntity<DeliveryDTO> updateDelivery(@PathVariable("id") String id, @Valid @RequestBody DeliveryDTO deliveryDTO) throws IllegalAccessException {
        return ResponseEntity.ok(deliveryService.updateDelivery(id, deliveryDTO));
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

}
