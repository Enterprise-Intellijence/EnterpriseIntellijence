package com.enterpriseintellijence.enterpriseintellijence.data.services;


import com.enterpriseintellijence.enterpriseintellijence.data.entities.Offer;
import com.enterpriseintellijence.enterpriseintellijence.data.entities.User;
import com.enterpriseintellijence.enterpriseintellijence.data.repository.OfferRepository;
import com.enterpriseintellijence.enterpriseintellijence.dto.OfferDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.UserDTO;
import com.enterpriseintellijence.enterpriseintellijence.exception.IdMismatchException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
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

        UserDTO userDTO = userService.findUserFromContext()
                .orElseThrow(EntityNotFoundException::new);

        offer.setOfferer(modelMapper.map(userDTO, User.class));

        Offer savedOffer = offerRepository.save(offer);

        return mapToDTO(savedOffer);
    }

    @Override
    public OfferDTO replaceOffer(String id, OfferDTO offerDTO) throws IllegalAccessException {
        throwOnIdMismatch(id, offerDTO);

        Offer oldOffer = offerRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        Offer newOffer = mapToEntity(offerDTO);

        UserDTO requestingUser = userService.findUserFromContext()
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
    public OfferDTO updateOffer(String id, JsonPatch patch) throws JsonPatchException {
        OfferDTO offer = mapToDTO(offerRepository.findById(id).orElseThrow(EntityNotFoundException::new));
        offer = applyPatch(patch, mapToEntity(offer));
        offerRepository.save(mapToEntity(offer));
        return offer;
    }

    @Override
    public OfferDTO deleteOffer(String id) {
        Offer offer = offerRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        offerRepository.deleteById(id);

        return mapToDTO(offer);
    }

    @Override
    public OfferDTO getOffer(String id) throws IllegalAccessException {
        Offer offer = offerRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        OfferDTO offerDTO = mapToDTO(offer);

        UserDTO userDTO = userService.findUserFromContext()
                .orElseThrow(EntityNotFoundException::new);

        if(!userDTO.getId().equals(offerDTO.getOfferer().getId())) {
            throw new IllegalAccessException("User cannot read other's offers");
        }
        return mapToDTO(offer);
    }

    public OfferDTO applyPatch(JsonPatch patch, Offer offer) throws JsonPatchException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode patched = patch.apply(objectMapper.convertValue(offer, JsonNode.class));

        return objectMapper.convertValue(patched, OfferDTO.class);
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
