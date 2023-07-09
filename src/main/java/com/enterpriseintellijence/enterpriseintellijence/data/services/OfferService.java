package com.enterpriseintellijence.enterpriseintellijence.data.services;

import com.enterpriseintellijence.enterpriseintellijence.dto.OfferDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.creation.OfferCreateDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.OfferState;


public interface OfferService {
    public OfferDTO createOffer(OfferCreateDTO offerCreateDTO) throws IllegalAccessException;

    OfferDTO updateOfferState(String id, OfferState state) throws IllegalAccessException;

    OfferDTO getOffer(String id) throws IllegalAccessException;
}
