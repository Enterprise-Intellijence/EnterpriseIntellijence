package com.enterpriseintellijence.enterpriseintellijence.data.services;

import com.enterpriseintellijence.enterpriseintellijence.dto.OfferDTO;
import org.springframework.http.ResponseEntity;

public interface OfferService {
    public OfferDTO createOffer(OfferDTO offerDTO);
    public ResponseEntity<OfferDTO> replaceOffer(String id, OfferDTO offerDTO);
    public ResponseEntity<OfferDTO> updateOffer(String id, OfferDTO offerDTO);
    ResponseEntity<OfferDTO> deleteOffer(String id);
    ResponseEntity<OfferDTO> getOffer(String id);
}
