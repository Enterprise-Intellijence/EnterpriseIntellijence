package com.enterpriseintellijence.enterpriseintellijence.data.services;


import com.enterpriseintellijence.enterpriseintellijence.core.services.NotificationSystem;
import com.enterpriseintellijence.enterpriseintellijence.core.services.ProcessSaleServiceImp;
import com.enterpriseintellijence.enterpriseintellijence.data.entities.Message;
import com.enterpriseintellijence.enterpriseintellijence.data.entities.Offer;
import com.enterpriseintellijence.enterpriseintellijence.data.entities.Product;
import com.enterpriseintellijence.enterpriseintellijence.data.entities.User;
import com.enterpriseintellijence.enterpriseintellijence.data.repository.MessageRepository;
import com.enterpriseintellijence.enterpriseintellijence.data.repository.OfferRepository;
import com.enterpriseintellijence.enterpriseintellijence.data.repository.ProductRepository;
import com.enterpriseintellijence.enterpriseintellijence.dto.OfferDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.creation.OfferCreateDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.Availability;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.OfferState;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.UserRole;
import com.enterpriseintellijence.enterpriseintellijence.exception.IdMismatchException;

import com.enterpriseintellijence.enterpriseintellijence.security.JwtContextUtils;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Clock;

@Service
@RequiredArgsConstructor
public class OfferServiceImp implements OfferService {

    private final ModelMapper modelMapper;

    private final UserService userService;
    private final MessageRepository messageRepository;

    private final OfferRepository offerRepository;
    private final JwtContextUtils jwtContextUtils;
    private final ProductRepository productRepository;
    private final Clock clock;
    private final NotificationSystem notificationSystem;
    private final ProcessSaleServiceImp processSaleServiceImp;
    private final NotificationService notificationService;


    @Override
    @Transactional
    public OfferDTO createOffer(OfferCreateDTO offerCreateDTO) throws IllegalAccessException {
        User loggedUser = jwtContextUtils.getUserLoggedFromContext();
        Product product = productRepository.findById(offerCreateDTO.getProduct().getId()).orElseThrow();

        if (!product.getAvailability().equals(Availability.AVAILABLE))
            throw new IllegalAccessException("Cannot create an offer");

        if(loggedUser.getId().equals(product.getSeller().getId()))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Cannot do offer for your product");

        Offer offer = processSaleServiceImp.CreateOffer(offerCreateDTO, product, loggedUser);
        Offer savedOffer = offerRepository.save(offer);

        Message message = notificationSystem.offerCreatedNotification(offer);
        messageRepository.save(message);

        notificationService.notifyOffer(savedOffer);

        return mapToDTO(savedOffer);
    }

    @Override
    public OfferDTO updateOfferState(String id, OfferState state) throws IllegalAccessException {
        if (state == null) {
            throw new IllegalAccessException("Cannot modify offer with null state");
        }

        User loggedUser = jwtContextUtils.getUserLoggedFromContext();
        Offer offer = offerRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        Product product = offer.getProduct();


        boolean isOfferByLoggedUser = offer.getOfferer().getId().equals(loggedUser.getId());
        boolean isOfferForLoggedUser = offer.getProduct().getSeller().getId().equals(loggedUser.getId());


        if (!loggedUser.isAdministrator()) {

            if (!offer.getState().equals(OfferState.PENDING)) {
                throw new IllegalAccessException("Cannot modify offer that is not pending");
            }

            if (!isOfferByLoggedUser && !isOfferForLoggedUser)
                throw new IllegalAccessException("Cannot modify offer");

            if (!product.getAvailability().equals(Availability.AVAILABLE)) {
                throw new IllegalAccessException("Cannot modify offer for a product that is not available");
            }
        }

        if (loggedUser.isAdministrator()) {
            offer.setState(state);
        } else {
            if (isOfferByLoggedUser) {
                // if Offer is By LoggedUser, then LoggedUser can only Cancel the Offer
                if (state.equals(OfferState.CANCELLED))
                    offer.setState(state);
            } else {
                // if Offer is For LoggedUser, then LoggedUser can only Accept or Reject the Offer
                if (state.equals(OfferState.ACCEPTED))
                {
                    offer.setState(state);
                    offer.getProduct().setAvailability(Availability.PENDING);

                }
                else if (state.equals(OfferState.REJECTED))
                    offer.setState(state);
            }
        }

        offer.getMessage().setOffer(offer);
        offerRepository.save(offer);

        notificationService.notifyOffer(offer);
        return mapToDTO(offer);
    }

    @Override
    public OfferDTO getOffer(String id) throws IllegalAccessException {
        Offer offer = offerRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        User loggedUser = jwtContextUtils.getUserLoggedFromContext();
        if (!loggedUser.getRole().equals(UserRole.USER) || loggedUser.getId().equals(offer.getOfferer().getId()) || loggedUser.getId().equals(offer.getProduct().getSeller().getId()))
            return mapToDTO(offer);
        else
            throw new IllegalAccessException("User cannot read other's offers");
    }

    private Offer mapToEntity(OfferDTO offerDTO) {
        return modelMapper.map(offerDTO, Offer.class);
    }

    private OfferDTO mapToDTO(Offer offer) {
        return modelMapper.map(offer, OfferDTO.class);
    }


    private void throwOnIdMismatch(String id, OfferDTO offerDTO) {
        if (!offerDTO.getId().equals(id)) {
            throw new IdMismatchException();
        }
    }
}
