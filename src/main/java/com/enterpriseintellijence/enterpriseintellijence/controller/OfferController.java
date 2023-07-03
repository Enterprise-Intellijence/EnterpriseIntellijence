package com.enterpriseintellijence.enterpriseintellijence.controller;


import com.enterpriseintellijence.enterpriseintellijence.data.services.OfferService;
import com.enterpriseintellijence.enterpriseintellijence.dto.OfferDTO;

import com.enterpriseintellijence.enterpriseintellijence.dto.creation.OfferCreateDTO;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.OfferState;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

import static com.enterpriseintellijence.enterpriseintellijence.security.AppSecurityConfig.SECURITY_CONFIG_NAME;


@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/offers", produces = "application/json")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@SecurityRequirement(name = SECURITY_CONFIG_NAME)
public class OfferController {
    // TODO: 16/05/23 Erne

    private final OfferService offerService;


    @PostMapping(consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public OfferDTO createOffer(@Valid @RequestBody OfferCreateDTO offerCreateDTO) throws IllegalAccessException {
        return offerService.createOffer(offerCreateDTO);
    }

    @PutMapping(path = "/{id}", consumes = "application/json")
    public OfferDTO replaceOffer(@PathVariable("id") String id, @Valid @RequestBody OfferDTO offerDTO) throws IllegalAccessException {
        return offerService.replaceOffer(id, offerDTO);
    }

    @PatchMapping(path = "/{id}/accept")
    public OfferDTO acceptOffer(@PathVariable("id") String id) throws IllegalAccessException {
        return offerService.updateOfferState(id, OfferState.ACCEPTED);
    }
    @PatchMapping(path = "/{id}/reject")
    public OfferDTO rejectOffer(@PathVariable("id") String id) throws IllegalAccessException {
        return offerService.updateOfferState(id, OfferState.REJECTED);
    }
    @PatchMapping(path = "/{id}/cancel")
    public OfferDTO cancelOffer(@PathVariable("id") String id) throws IllegalAccessException {
        return offerService.updateOfferState(id, OfferState.CANCELLED);
    }

    @PatchMapping(path = "/{id}/state", consumes = "application/json")
    public OfferDTO setOfferState(@PathVariable("id") String id, @Valid @RequestBody OfferState state) throws IllegalAccessException {
        return offerService.updateOfferState(id, state);
    }

    @PatchMapping(path = "/{id}", consumes = "application/json")
    public ResponseEntity<OfferDTO> updateOffer(@PathVariable("id") String id, @Valid @RequestBody OfferDTO patch) throws IllegalAccessException {
        return ResponseEntity.ok(offerService.updateOffer(id, patch));
    }


    @DeleteMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteOffer(@PathVariable("id") String id) throws IllegalAccessException {
        offerService.deleteOffer(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<OfferDTO> getOffer(@PathVariable("id") String id) throws IllegalAccessException {
        return ResponseEntity.ok(offerService.getOffer(id));
    }
}
