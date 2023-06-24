package com.enterpriseintellijence.enterpriseintellijence.core.services;

import com.enterpriseintellijence.enterpriseintellijence.data.entities.Message;
import com.enterpriseintellijence.enterpriseintellijence.data.entities.Offer;
import com.enterpriseintellijence.enterpriseintellijence.data.entities.Product;
import com.enterpriseintellijence.enterpriseintellijence.data.entities.User;
import com.enterpriseintellijence.enterpriseintellijence.data.repository.MessageRepository;
import com.enterpriseintellijence.enterpriseintellijence.data.services.MessageService;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.MessageStatus;
import com.enterpriseintellijence.enterpriseintellijence.security.JwtContextUtils;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationSystem {

    private final MessageRepository messageRepository;
    private final MessageService messageService;
    private final JwtContextUtils jwtContextUtils;
    private final Clock clock;

    public Message offerCreatedNotification(Offer offer){

        Product product = offer.getProduct();


        String convID = messageService.getConversationId(offer.getOfferer(), product.getSeller(), product);


        return Message.builder()
                .text("Hi, here is my offer for " + product.getTitle() + ".")
                .messageDate(LocalDateTime.now(clock))
                .messageStatus(MessageStatus.UNREAD)
                .product(product)
                .conversationId(convID)
                .sendUser(offer.getOfferer())
                .receivedUser(product.getSeller())
                .offer(offer)
                .build();
    }

    public Message offerAcceptedOrRejectedNotification(Offer offer, boolean isAccepted) {
        String basic = "Your offer for product " +offer.getProduct().getTitle();
        if (isAccepted)
            basic = basic + " is accepted. Please check your profile and complete the order";
        else
            basic = basic + "was rejected. Sorry, try again.";


        return Message.builder()
                .text(basic)
                .messageDate(LocalDateTime.now(clock))
                .messageStatus(MessageStatus.UNREAD)
                .conversationId(offer.getMessage().getConversationId())
                .product(offer.getProduct())
                .sendUser(offer.getOfferer())
                .receivedUser(offer.getProduct().getSeller())
                .offer(offer)
                .build();

    }

}
