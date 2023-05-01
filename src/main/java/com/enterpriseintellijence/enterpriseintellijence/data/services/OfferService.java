package com.enterpriseintellijence.enterpriseintellijence.data.services;

import com.enterpriseintellijence.enterpriseintellijence.dto.OfferDTO;


public interface OfferService {
    public OfferDTO createOffer(OfferDTO offerDTO);
    public OfferDTO replaceOffer(String id, OfferDTO offerDTO) throws IllegalAccessException;
    OfferDTO updateOffer(String id, OfferDTO patch);
    OfferDTO deleteOffer(String id);
    OfferDTO getOffer(String id) throws IllegalAccessException;
}
