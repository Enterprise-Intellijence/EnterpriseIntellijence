package com.enterpriseintellijence.enterpriseintellijence.controller;


import com.enterpriseintellijence.enterpriseintellijence.data.services.OfferService;
import com.enterpriseintellijence.enterpriseintellijence.dto.OfferDTO;

import com.enterpriseintellijence.enterpriseintellijence.dto.creation.OfferCreateDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.OfferState;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/offers", produces = "application/json")
@CrossOrigin(origins = "*", allowedHeaders = "*")
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

    @PutMapping(path = "/{id}/accept", consumes = "application/json")
    public OfferDTO acceptOffer(@PathVariable("id") String id, @Valid @RequestBody OfferDTO offerDTO) throws IllegalAccessException {
        return offerService.updateOfferState(id, OfferState.ACCEPTED);
    }
    @PutMapping(path = "/{id}/reject", consumes = "application/json")
    public OfferDTO rejectOffer(@PathVariable("id") String id, @Valid @RequestBody OfferDTO offerDTO) throws IllegalAccessException {
        return offerService.updateOfferState(id, OfferState.REJECTED);
    }
    @PutMapping(path = "/{id}/cancel", consumes = "application/json")
    public OfferDTO cancelOffer(@PathVariable("id") String id, @Valid @RequestBody OfferDTO offerDTO) throws IllegalAccessException {
        return offerService.updateOfferState(id, OfferState.CANCELLED);
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
