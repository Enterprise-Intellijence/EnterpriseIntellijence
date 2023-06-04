package com.enterpriseintellijence.enterpriseintellijence.core.services;

import com.enterpriseintellijence.enterpriseintellijence.data.entities.*;
import com.enterpriseintellijence.enterpriseintellijence.data.entities.embedded.CustomMoney;
import com.enterpriseintellijence.enterpriseintellijence.dto.DeliveryDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.OfferDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.TransactionDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.creation.OfferCreateDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import javax.management.modelmbean.ModelMBean;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class ProcessSaleServiceImp implements ProcessSaleService{
    private final Clock clock;
    private final ModelMapper modelMapper;
    private final NotificationSystem notificationSystem;

    @Override
    public Offer madeAnOffer(OfferCreateDTO offerCreateDTO, Product product, User loggedUser) {
        Offer offer = new Offer();
        offer.setAmount(modelMapper.map(offerCreateDTO.getAmount(), CustomMoney.class));
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

    @Override
    public Order buyProduct(Product product, User loggedUser) {
        Order order = new Order();

        if(product.getOffers()!=null && !product.getOffers().isEmpty()){
            //check se sto comprando dopo aver fatto un'offerta che è stata accettata;
            for(Offer offer: product.getOffers()){
                if(offer.getOfferer().getId().equals(loggedUser.getId()) && offer.getState().equals(OfferState.ACCEPTED)) {
                    order.setOffer(offer);
                    offer.setOrder(order);
                }
            }
        }

        LocalDateTime now = timeNow();
        order.setOrderDate(now);
        order.setOrderUpdateDate(now);

        order.setState(OrderState.PENDING);

        product.setAvailability(Availability.PENDING);
        product.setLastUpdateDate(now);
        product.setOrder(order);
        order.setProduct(product);

        order.setUser(loggedUser);

        return order;
    }

    @Override
    public Order cancelOrder(Order order, User loggedUser) {
        LocalDateTime now = timeNow();
        order.getProduct().setAvailability(Availability.AVAILABLE);
        order.getProduct().setLastUpdateDate(now);
        order.setState(OrderState.CANCELED);
        order.setOrderUpdateDate(now);
        order.setUser(null);
        if(order.getOffer()!=null){
            order.getOffer().setState(OfferState.REJECTED);
            order.setOffer(null);
        }
        // TODO: 25/05/2023 send notification

        return order;
    }

    @Override
    public Transaction payProduct(Order order, User loggedUser, PaymentMethod paymentMethod) {
        Transaction transaction = new Transaction();

        LocalDateTime now = timeNow();

        Double productPrice = order.getProduct().getProductCost().getPrice();
        if(order.getOffer()!=null)
            productPrice = order.getOffer().getAmount().getPrice();
        Double amount = productPrice+order.getProduct().getDeliveryCost().getPrice();
        transaction.setAmount(new CustomMoney(amount,order.getProduct().getProductCost().getCurrency()));
        transaction.setOrder(order);
        transaction.setCreationTime(now);
        order.setOrderUpdateDate(now);
        Random random = new Random();
        if(random.nextInt(101)>=90)
            transaction.setTransactionState(TransactionState.REJECTED);
        else{
            transaction.setTransactionState(TransactionState.COMPLETED);
            order.setState(OrderState.PURCHASED);
            order.setOrderUpdateDate(now);
            order.getProduct().setAvailability(Availability.UNAVAILABLE);
            order.getProduct().setLastUpdateDate(now);
        }

        return transaction;
    }

    @Override
    public Delivery sendProduct(Order order, User loggedUser, String shipper) {
        Delivery delivery = new Delivery();
        LocalDateTime now = timeNow();

        delivery.setSendTime(now);
        order.setState(OrderState.SHIPPED);
        order.setOrderUpdateDate(now);
        delivery.setOrder(order);
        order.setDelivery(delivery);

        delivery.setDeliveryStatus(DeliveryStatus.SHIPPED);
        delivery.setDeliveryCost(order.getProduct().getDeliveryCost());

        // TODO: 03/06/2023 occhio indirizzo
        delivery.setShipper(shipper);
        delivery.setSenderAddress(loggedUser.getDefaultAddress());
        delivery.setReceiverAddress(order.getUser().getDefaultAddress());

        return delivery;
    }

    @Override
    public Delivery productDelivered(Order order, User loggedUser, Delivery delivery) {
        delivery.setDeliveryStatus(DeliveryStatus.DELIVERED);
        order.setState(OrderState.DELIVERED);
        order.setOrderUpdateDate(timeNow());
        order.getProduct().setLastUpdateDate(timeNow());
        delivery.setOrder(order);
        delivery.setDeliveredTime(timeNow());

        return delivery;
    }

    @Override
    public Order completeOrder(Order order, User loggedUser) {
        order.setState(OrderState.COMPLETED);
        order.setOrderUpdateDate(timeNow());
        order.getProduct().setLastUpdateDate(timeNow());
        return order;
    }

    private LocalDateTime timeNow(){
        return LocalDateTime.now(clock);
    }
}
