package com.enterpriseintellijence.enterpriseintellijence.controller;


import com.enterpriseintellijence.enterpriseintellijence.data.services.OfferService;
import com.enterpriseintellijence.enterpriseintellijence.dto.OfferDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/offers", produces = "application/json")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class OfferController {

    private final OfferService offerService;

    @PostMapping(consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public OfferDTO createOffer(@RequestBody OfferDTO offerDTO) {
        return offerService.createOffer(offerDTO);
    }

    @PutMapping(path = "/{id}", consumes = "application/json")
    public OfferDTO replaceOffer(@PathVariable("id") String id, @RequestBody OfferDTO offerDTO) {
        return offerService.replaceOffer(id, offerDTO).getBody();
    }

    @PatchMapping(path = "/{id}", consumes = "application/json")
    public ResponseEntity<OfferDTO> updateOffer(@PathVariable("id") String id, @RequestBody OfferDTO offerDTO) {
        return offerService.updateOffer(id, offerDTO);
    }

    @DeleteMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<OfferDTO> deleteOffer(@PathVariable("id") String id) {
        return offerService.deleteOffer(id);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<OfferDTO> getOffer(@PathVariable("id") String id) {
        return offerService.getOffer(id);
    }
}
