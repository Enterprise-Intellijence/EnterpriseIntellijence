package com.enterpriseintellijence.enterpriseintellijence.core.services;

import com.enterpriseintellijence.enterpriseintellijence.data.entities.*;
import com.enterpriseintellijence.enterpriseintellijence.data.entities.embedded.CustomMoney;
import com.enterpriseintellijence.enterpriseintellijence.data.repository.OrderRepository;
import com.enterpriseintellijence.enterpriseintellijence.data.services.NotificationService;
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
    private final NotificationService notificationService;
    private final OrderRepository orderRepository;

    @Override
    public Offer CreateOffer(OfferCreateDTO offerCreateDTO, Product product, User loggedUser) {
        Offer offer = new Offer();
        offer.setAmount(modelMapper.map(offerCreateDTO.getAmount(), CustomMoney.class));
        offer.setCreationTime(timeNow());
        offer.setState(OfferState.PENDING);
//        loggedUser.getOffersMade().add(offer);
        offer.setOfferer(loggedUser);
        product.setLastUpdateDate(timeNow());
        offer.setProduct(product);

//        offer.setMessage(notificationSystem.offerCreatedNotification(offer));


        return offer;
    }

    // TODO: 09/07/2023 cancelliamo??
    @Override
    public Offer acceptOrRejectAnOffer(Offer offer, OfferDTO offerDTO, Product product, User loggedUser, boolean isAccepted) {
        if(!offer.getState().equals(OfferState.PENDING)) {
            throw new EntityNotFoundException("Offer is not pending");
        }

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
    public Order buyProduct(Product product, User loggedUser,Address deliveryAddress) {
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
        order.setDeliveryAddress(deliveryAddress);
        product.setAvailability(Availability.PENDING);
        product.setLastUpdateDate(now);
        // TODO: 11/07/2023 tenere d'occhio al momento del save
        product.getOrder().add(order);
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
        if(order.getOffer()!=null){
            order.getOffer().setState(OfferState.CANCELLED);
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
        //transaction.setOrder(order);
        transaction.setCreationTime(now);
        order.setOrderUpdateDate(now);
        transaction.setPaymentMethod(paymentMethod.getCreditCard());
        transaction.setPaymentMethodOwner(paymentMethod.getOwner());
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
        orderRepository.save(order);
        return transaction;
    }

    @Override
    public Delivery sendProduct(Order order, User loggedUser, String shipper) {
        Delivery delivery = new Delivery();
        LocalDateTime now = timeNow();

        delivery.setSendTime(now);
        order.setState(OrderState.SHIPPED);
        order.setOrderUpdateDate(now);

        //delivery.setOrder(order);

        order.setDelivery(delivery);

        delivery.setDeliveryStatus(DeliveryStatus.SHIPPED);
        delivery.setDeliveryCost(order.getProduct().getDeliveryCost());

        // TODO: 03/06/2023 occhio indirizzo
        delivery.setShipper(shipper);

        for (Address address:loggedUser.getAddresses()){
            if(address.isDefault())
                delivery.setSenderAddress(address);
        }
        delivery.setReceiverAddress(order.getDeliveryAddress());

        orderRepository.save(order);

        return delivery;
    }

    @Override
    public Delivery productDelivered(Order order, User loggedUser, Delivery delivery) {
        delivery.setDeliveryStatus(DeliveryStatus.DELIVERED);
        order.setState(OrderState.DELIVERED);
        order.setOrderUpdateDate(timeNow());
        order.getProduct().setLastUpdateDate(timeNow());
        //delivery.setOrder(order);

        delivery.setDeliveredTime(timeNow());

        orderRepository.save(order);
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
