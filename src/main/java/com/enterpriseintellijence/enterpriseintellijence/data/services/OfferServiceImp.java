package com.enterpriseintellijence.enterpriseintellijence.data.services;


import com.enterpriseintellijence.enterpriseintellijence.core.services.NotificationSystem;
import com.enterpriseintellijence.enterpriseintellijence.core.services.ProcessSaleServiceImp;
import com.enterpriseintellijence.enterpriseintellijence.data.entities.Message;
import com.enterpriseintellijence.enterpriseintellijence.data.entities.Offer;
import com.enterpriseintellijence.enterpriseintellijence.data.entities.Product;
import com.enterpriseintellijence.enterpriseintellijence.data.entities.User;
import com.enterpriseintellijence.enterpriseintellijence.data.entities.embedded.CustomMoney;
import com.enterpriseintellijence.enterpriseintellijence.data.repository.OfferRepository;
import com.enterpriseintellijence.enterpriseintellijence.data.repository.ProductRepository;
import com.enterpriseintellijence.enterpriseintellijence.dto.OfferDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.UserDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.creation.OfferCreateDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.Availability;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.MessageStatus;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.OfferState;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.UserRole;
import com.enterpriseintellijence.enterpriseintellijence.exception.IdMismatchException;

import com.enterpriseintellijence.enterpriseintellijence.security.JwtContextUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OfferServiceImp implements OfferService {

    private final ModelMapper modelMapper;

    private final UserService userService;

    private final OfferRepository offerRepository;
    private final JwtContextUtils jwtContextUtils;
    private final ProductRepository productRepository;
    private final Clock clock;
    private final NotificationSystem notificationSystem;
    private final ProcessSaleServiceImp processSaleServiceImp;
    private final NotificationService notificationService;


    @Override
    public OfferDTO createOffer(OfferCreateDTO offerCreateDTO) throws IllegalAccessException {
        User loggedUser = jwtContextUtils.getUserLoggedFromContext();
        Product product = productRepository.findById(offerCreateDTO.getProduct().getId()).orElseThrow();
        User seller = product.getSeller();

        if(!product.getAvailability().equals(Availability.AVAILABLE) )
            throw new IllegalAccessException("Cannot create an offer");

        Offer offer = processSaleServiceImp.madeAnOffer(offerCreateDTO,product,loggedUser);

        Offer savedOffer = offerRepository.save(offer);

        notificationService.notifyOffer(savedOffer);

        return mapToDTO(savedOffer);
    }

    @Override
    public OfferDTO replaceOffer(String id, OfferDTO offerDTO) throws IllegalAccessException {
        // TODO: 23/05/2023 per me non ha senso di esistere
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
    public OfferDTO updateOffer(String id, OfferDTO patch,boolean isOffer, boolean isAccepted) throws IllegalAccessException {
        throwOnIdMismatch(id,patch);
        User loggedUser = jwtContextUtils.getUserLoggedFromContext();
        Offer offer = offerRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        Product product = offer.getProduct();

        if(loggedUser.getRole().equals(UserRole.USER) && isOffer && !loggedUser.getId().equals(patch.getOfferer().getId()))
            throw new IllegalAccessException("Cannot modify offer");

        if(loggedUser.getRole().equals(UserRole.USER) && !isOffer && !loggedUser.getId().equals(patch.getProduct().getSeller().getId()))
            throw new IllegalAccessException("Cannot modify offer");

        if(isOffer && product.getAvailability().equals(Availability.AVAILABLE)){
            if(patch.getAmount()!= null && (!patch.getAmount().getPrice().equals(offer.getAmount().getPrice()) || !patch.getAmount().getCurrency().equals(offer.getAmount().getCurrency()) )){
                offer.setAmount(modelMapper.map(patch.getAmount(),CustomMoney.class));
                offer.setState(OfferState.PENDING);
                offer.setMessage(notificationSystem.offerCreatedNotification(offer));
            }
        }
        else if(!isOffer){
            processSaleServiceImp.acceptOrRejectAnOffer(offer,patch,product,loggedUser,isAccepted);
        }

        offerRepository.save(offer);
        notificationService.notifyOffer(offer);
        return mapToDTO(offer);
    }



    @Override
    public void deleteOffer(String id) throws IllegalAccessException {
        User loggedUser = jwtContextUtils.getUserLoggedFromContext();
        Offer offer = offerRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        if(loggedUser.getRole().equals(UserRole.USER) && !loggedUser.getId().equals(offer.getOfferer().getId()))
            throw new IllegalAccessException("Cannot delete offer");

        if(offer.getState().equals(OfferState.ACCEPTED))
            throw new IllegalAccessException("Cannot delete offer accepted");

        offerRepository.delete(offer);
    }

    @Override
    public OfferDTO getOffer(String id) throws IllegalAccessException {
        Offer offer = offerRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        User loggedUser = jwtContextUtils.getUserLoggedFromContext();
        if(!loggedUser.getRole().equals(UserRole.USER) || loggedUser.getId().equals(offer.getOfferer().getId()) || loggedUser.getId().equals(offer.getProduct().getSeller().getId()) )
            return mapToDTO(offer);
        else
            throw new IllegalAccessException("User cannot read other's offers");
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
