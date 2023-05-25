package com.enterpriseintellijence.enterpriseintellijence.core.services;

import com.enterpriseintellijence.enterpriseintellijence.data.entities.Offer;
import com.enterpriseintellijence.enterpriseintellijence.data.entities.Product;
import com.enterpriseintellijence.enterpriseintellijence.data.entities.User;
import com.enterpriseintellijence.enterpriseintellijence.data.entities.embedded.CustomMoney;
import com.enterpriseintellijence.enterpriseintellijence.dto.OfferDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.Availability;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.OfferState;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import javax.management.modelmbean.ModelMBean;
import java.time.Clock;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ProcessSaleServiceImp implements ProcessSaleService{
    private final Clock clock;
    private final ModelMapper modelMapper;
    private final NotificationSystem notificationSystem;

    @Override
    public Offer madeAnOffer(OfferDTO offerDTO, Product product, User loggedUser) {
        Offer offer = new Offer();
        offer.setAmount(modelMapper.map(offerDTO.getAmount(), CustomMoney.class));
        offer.setCreationTime(timeNow());
        offer.setState(OfferState.PENDING);
        loggedUser.getOffersMade().add(offer);
        offer.setOfferer(loggedUser);
        product.setLastUpdateDate(timeNow());
        offer.setProduct(product);
        // TODO: 25/05/2023 aggiungere un campo in user per i messaggi non letti?
        offer.setMessage(notificationSystem.offerCreatedNotification(offer,product));

        return offer;
    }

    @Override
    public Offer acceptOrRejectAnOffer(Offer offer, OfferDTO offerDTO, Product product, User loggedUser, boolean isAccepted) {
        if(isAccepted){
            offer.setState(OfferState.ACCEPTED);
            product.setAvailability(Availability.PENDING);
            product.setLastUpdateDate(timeNow());
            offer.setProduct(product);
        }
        else
            offer.setState(OfferState.REJECTED);

        offer.setMessage(notificationSystem.offerAcceptedOrRejectedNotification(offer,isAccepted));
        return null;
    }

    private LocalDateTime timeNow(){
        return LocalDateTime.now(clock);
    }
}
