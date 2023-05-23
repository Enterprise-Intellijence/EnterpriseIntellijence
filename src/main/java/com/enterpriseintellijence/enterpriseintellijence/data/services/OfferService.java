package com.enterpriseintellijence.enterpriseintellijence.data.services;

import com.enterpriseintellijence.enterpriseintellijence.dto.OfferDTO;


public interface OfferService {
    public OfferDTO createOffer(OfferDTO offerDTO) throws IllegalAccessException;
    public OfferDTO replaceOffer(String id, OfferDTO offerDTO) throws IllegalAccessException;
    OfferDTO updateOffer(String id, OfferDTO patch,boolean isOffererer,boolean isAccepted) throws IllegalAccessException;
    void deleteOffer(String id) throws IllegalAccessException;
    OfferDTO getOffer(String id) throws IllegalAccessException;
}
