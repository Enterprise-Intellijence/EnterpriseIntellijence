package com.enterpriseintellijence.enterpriseintellijence.core.services;

import com.enterpriseintellijence.enterpriseintellijence.data.entities.*;
import com.enterpriseintellijence.enterpriseintellijence.dto.OfferDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.creation.OfferCreateDTO;
import org.springframework.stereotype.Service;

@Service
public interface ProcessSaleService {

    Offer CreateOffer(OfferCreateDTO offerCreateDTO, Product product, User loggedUser);
    Offer acceptOrRejectAnOffer(Offer offer, OfferDTO offerDTO, Product product, User loggedUser,boolean isAccepted);

    Order buyProduct(Product product,User loggedUser,Address deliveryAddress);
    Order cancelOrder(Order order,User loggedUser);

    Transaction payProduct(Order order, User loggedUser, PaymentMethod paymentMethod);

    Delivery sendProduct(Order order, User loggedUser, String shipper, Address senderAddress);

    Delivery productDelivered(Order order,User loggedUser,Delivery delivery);

    Order completeOrder(Order order,User loggedUser);//invocato dal compratore per settare l'ordine su completed e sbloccare la recensione


}
