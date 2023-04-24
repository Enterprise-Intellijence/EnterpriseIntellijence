package com.enterpriseintellijence.enterpriseintellijence.data.services;


import com.enterpriseintellijence.enterpriseintellijence.data.entities.Offer;
import com.enterpriseintellijence.enterpriseintellijence.data.repository.OfferDAO;
import com.enterpriseintellijence.enterpriseintellijence.dto.OfferDTO;
import com.enterpriseintellijence.enterpriseintellijence.exception.IdMismatchException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OfferServiceImp implements OfferService {

    private final ModelMapper modelMapper;
    private final OfferDAO offerRepository;

    @Override
    public OfferDTO createOffer(OfferDTO offerDTO) {
        Offer offer = mapToEntity(offerDTO);
        offer = offerRepository.save(offer);

        return mapToDTO(offer);
    }

    @Override
    public OfferDTO replaceOffer(String id, OfferDTO offerDTO) {
        throwOnIdMismatch(id, offerDTO);
        Offer offer = mapToEntity(offerDTO);
        offer = offerRepository.save(offer);

        return mapToDTO(offer);
    }

    @Override
    public OfferDTO updateOffer(String id, OfferDTO offerDTO) {
        //TODO: Implement this method
        return null;
    }

    @Override
    public OfferDTO deleteOffer(String id) {
        Optional<Offer> offer = offerRepository.findById(id);

        if(!offer.isPresent()) {
            throw new EntityNotFoundException("Offer not found");
        }
        offerRepository.deleteById(id);

        return mapToDTO(offer.get());
    }

    @Override
    public OfferDTO getOffer(String id) {
        Optional<Offer> offer = offerRepository.findById(id);

        if(!offer.isPresent()) {
            throw new EntityNotFoundException("Offer not found");
        }
        return mapToDTO(offer.get());
    }


    // TODO: VA TESTATA ASSOLUTAMENTE
    private Iterable<OfferDTO> mapToDTO(Iterable<Offer> offers) {
        Iterable<OfferDTO> offerDTOs = new ArrayList<>();
        modelMapper.map(offers,offerDTOs);
        return offerDTOs;
    }

    private Offer mapToEntity(OfferDTO offerDTO) {
        return modelMapper.map(offerDTO,Offer.class);
    }

    private OfferDTO mapToDTO(Offer offer) {
        return modelMapper.map(offer,OfferDTO.class);
    }

    private void throwOnIdMismatch(String id, OfferDTO offerDTO) {
        if (offerDTO.getId() != null && !offerDTO.getId().equals(id)) {
            throw new IdMismatchException();
        }
    }
}
