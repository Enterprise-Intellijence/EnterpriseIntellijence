package com.enterpriseintellijence.enterpriseintellijence.data.services;

import com.enterpriseintellijence.enterpriseintellijence.dto.OfferDTO;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import org.springframework.http.ResponseEntity;

public interface OfferService {
    public OfferDTO createOffer(OfferDTO offerDTO);
    public OfferDTO replaceOffer(String id, OfferDTO offerDTO) throws IllegalAccessException;

    OfferDTO updateOffer(String id, JsonPatch patch) throws JsonPatchException;

    OfferDTO deleteOffer(String id);
    OfferDTO getOffer(String id) throws IllegalAccessException;
}
