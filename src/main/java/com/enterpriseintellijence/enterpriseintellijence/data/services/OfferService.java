package com.enterpriseintellijence.enterpriseintellijence.data.services;

import com.enterpriseintellijence.enterpriseintellijence.dto.OfferDTO;
import org.springframework.http.ResponseEntity;

public interface OfferService {
    public OfferDTO createOffer(OfferDTO offerDTO);
    public ResponseEntity<OfferDTO> replaceOffer(OfferDTO offerDTO);
    public ResponseEntity<OfferDTO> updateOffer(OfferDTO offerDTO);
    ResponseEntity<OfferDTO> deleteOffer(String id);
    ResponseEntity<OfferDTO> getOffer(String id);
}
