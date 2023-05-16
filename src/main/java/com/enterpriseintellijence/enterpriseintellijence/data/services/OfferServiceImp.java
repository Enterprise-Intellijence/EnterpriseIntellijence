package com.enterpriseintellijence.enterpriseintellijence.data.services;


import com.enterpriseintellijence.enterpriseintellijence.data.entities.Offer;
import com.enterpriseintellijence.enterpriseintellijence.data.entities.User;
import com.enterpriseintellijence.enterpriseintellijence.data.repository.OfferRepository;
import com.enterpriseintellijence.enterpriseintellijence.dto.OfferDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.UserFullDTO;
import com.enterpriseintellijence.enterpriseintellijence.exception.IdMismatchException;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OfferServiceImp implements OfferService {

    private final ModelMapper modelMapper;

    private final UserService userService;

    private final OfferRepository offerRepository;

    @Override
    public OfferDTO createOffer(OfferDTO offerDTO) {
        Offer offer = mapToEntity(offerDTO);

        UserFullDTO userFullDTO = userService.findUserFromContext()
                .orElseThrow(EntityNotFoundException::new);

        offer.setOfferer(modelMapper.map(userFullDTO, User.class));

        Offer savedOffer = offerRepository.save(offer);

        return mapToDTO(savedOffer);
    }

    @Override
    public OfferDTO replaceOffer(String id, OfferDTO offerDTO) throws IllegalAccessException {
        throwOnIdMismatch(id, offerDTO);

        Offer oldOffer = offerRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        Offer newOffer = mapToEntity(offerDTO);

        UserFullDTO requestingUser = userService.findUserFromContext()
                .orElseThrow(EntityNotFoundException::new);

        if(!requestingUser.getId().equals(oldOffer.getOfferer().getId())) {
            throw new IllegalAccessException("User cannot change offer");
        }
        if(!requestingUser.getId().equals(newOffer.getOfferer().getId())) {
            throw new IllegalAccessException("User cannot change offer");
        }
        if(!oldOffer.getState().equals(newOffer.getState())) {
            throw new IllegalAccessException("State cannot be changed");
        }

        newOffer = offerRepository.save(newOffer);

        return mapToDTO(newOffer);
    }

    @Override
    public OfferDTO updateOffer(String id, OfferDTO patch) {
        OfferDTO offer = mapToDTO(offerRepository.findById(id).orElseThrow(EntityNotFoundException::new));

        // TODO: Implement patching here

        offerRepository.save(mapToEntity(offer));
        return offer;
    }

    @Override
    public void deleteOffer(String id) {
        offerRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        offerRepository.deleteById(id);
    }

    @Override
    public OfferDTO getOffer(String id) throws IllegalAccessException {
        Offer offer = offerRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        OfferDTO offerDTO = mapToDTO(offer);

        UserFullDTO userFullDTO = userService.findUserFromContext()
                .orElseThrow(EntityNotFoundException::new);

        if(!userFullDTO.getId().equals(offerDTO.getOfferer().getId())) {
            throw new IllegalAccessException("User cannot read other's offers");
        }
        return mapToDTO(offer);
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
