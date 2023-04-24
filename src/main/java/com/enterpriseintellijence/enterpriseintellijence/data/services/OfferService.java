package com.enterpriseintellijence.enterpriseintellijence.data.services;

import com.enterpriseintellijence.enterpriseintellijence.dto.OfferDTO;
import org.springframework.http.ResponseEntity;

public interface OfferService {
    public OfferDTO createOffer(OfferDTO offerDTO);
    public OfferDTO replaceOffer(String id, OfferDTO offerDTO);
    public OfferDTO updateOffer(String id, OfferDTO offerDTO);
    OfferDTO deleteOffer(String id);
    OfferDTO getOffer(String id);
}
