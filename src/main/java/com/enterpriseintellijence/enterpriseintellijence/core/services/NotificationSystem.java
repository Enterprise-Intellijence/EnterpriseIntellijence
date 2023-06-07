package com.enterpriseintellijence.enterpriseintellijence.core.services;

import com.enterpriseintellijence.enterpriseintellijence.data.entities.Message;
import com.enterpriseintellijence.enterpriseintellijence.data.entities.Offer;
import com.enterpriseintellijence.enterpriseintellijence.data.entities.Product;
import com.enterpriseintellijence.enterpriseintellijence.data.entities.User;
import com.enterpriseintellijence.enterpriseintellijence.data.repository.MessageRepository;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.MessageStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationSystem {
    private final MessageRepository messageRepository;
    private final Clock clock;

    public Message offerCreatedNotification(Offer offer , Product product){
        String convID = UUID.randomUUID().toString();
        while(!messageRepository.canUseConversationId(convID))
            convID = UUID.randomUUID().toString();


        return Message.builder()
                .text("A new Offer is available for product: "+product.getTitle())
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
