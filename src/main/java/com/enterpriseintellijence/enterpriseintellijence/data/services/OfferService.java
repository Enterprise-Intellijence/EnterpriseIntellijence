package com.enterpriseintellijence.enterpriseintellijence.data.services;

import com.enterpriseintellijence.enterpriseintellijence.dto.OfferDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.creation.OfferCreateDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.OfferState;


public interface OfferService {
    public OfferDTO createOffer(OfferCreateDTO offerCreateDTO) throws IllegalAccessException;
    public OfferDTO replaceOffer(String id, OfferDTO offerDTO) throws IllegalAccessException;
    OfferDTO updateOffer(String id, OfferDTO patch) throws IllegalAccessException;

    OfferDTO updateOfferState(String id, OfferState state) throws IllegalAccessException;

    void deleteOffer(String id) throws IllegalAccessException;
    OfferDTO getOffer(String id) throws IllegalAccessException;
}
