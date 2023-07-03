package com.enterpriseintellijence.enterpriseintellijence.data.services;

import com.enterpriseintellijence.enterpriseintellijence.data.entities.*;
import com.enterpriseintellijence.enterpriseintellijence.data.repository.*;
import com.enterpriseintellijence.enterpriseintellijence.dto.NotificationDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.creation.NotificationText;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.NotificationType;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.OfferState;
import com.enterpriseintellijence.enterpriseintellijence.security.JwtContextUtils;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class NotificationServiceImp implements NotificationService {

    private final NotificationsRepository notificationRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ReviewRepository reviewRepository;
    private final OfferRepository offerRepository;
    private final JwtContextUtils jwtContextUtils;
    private final ModelMapper modelMapper;

    private NotificationDTO createNotification(Notification notification) {
        User receiver = userRepository.findById(notification.getReceiver().getId()).orElseThrow(() -> new IllegalArgumentException("Receiver not found"));
        if (notification.getType() == null) throw new IllegalArgumentException("Invalid notification");
        if (!notification.getType().equals(NotificationType.MESSAGE) &&
                (notification.getUserTarget() == null && notification.getProductTarget() == null && notification.getOfferTarget() == null)) throw new IllegalArgumentException("Invalid notification");
        if (notification.getUserTarget() != null && userRepository.findById(notification.getUserTarget()).isEmpty())
            throw new IllegalArgumentException("User target not found");
        if (notification.getProductTarget() != null && productRepository.findById(notification.getProductTarget()).isEmpty())
            throw new IllegalArgumentException("Product target not found");
        if (notification.getOfferTarget() != null)
        {
            Offer offer = offerRepository.findById(notification.getOfferTarget()).orElseThrow(() -> new IllegalArgumentException("Offer target not found"));
            if(!receiver.equals(offer.getOfferer()) && !receiver.equals(offer.getProduct().getSeller()))
                throw new IllegalArgumentException("Invalid offer target");
        }
        if(notification.getReviewTarget() != null )
        {
            Review review = reviewRepository.findById(notification.getReviewTarget()).orElseThrow(() -> new IllegalArgumentException("Review target not found"));
            if(!receiver.equals(review.getReviewed()) && !receiver.equals(review.getReviewer()))
                throw new IllegalArgumentException("Invalid review target");
        }
        notification.setDate(java.time.LocalDateTime.now());
        notification.setRead(false);
        switch (notification.getType()) {
            case NEW_OFFER -> notification.setText(NotificationText.NEW_OFFER);
            case OFFER_REJECTED -> notification.setText(NotificationText.OFFER_REJECTED);
            case OFFER_ACCEPTED -> notification.setText(NotificationText.OFFER_ACCEPTED);
            case REVIEW -> notification.setText(NotificationText.REVIEW);
            case MESSAGE -> notification.setText(NotificationText.MESSAGE);
            case SALE -> notification.setText(NotificationText.SALE);
            case PURCHASE -> notification.setText(NotificationText.PURCHASE);
            case PRODUCT_SOLD -> notification.setText(NotificationText.PRODUCT_SOLD);
            case NEW_PRODUCT -> notification.setText(NotificationText.NEW_PRODUCT);
            case FOLLOW -> notification.setText(NotificationText.FOLLOW);
            case NEW_FAVORITE -> notification.setText(NotificationText.NEW_FAVORITE);
        }

        return mapToDTO(notificationRepository.save(notification));
    }

    @Override
    public NotificationDTO notifyReview(Review review) {
        Notification notification = Notification.builder()
                .receiver(review.getReviewed())
                .reviewTarget(review.getId())
                .type(NotificationType.REVIEW)
                .build();
        return createNotification(notification);
    }

    @Override
    public NotificationDTO notifyOffer(Offer offer) {
        User receiver;
        NotificationType type;
        if (offer.getState().equals(OfferState.ACCEPTED)) {
            type = NotificationType.OFFER_ACCEPTED;
            receiver = offer.getOfferer();
        } else if (offer.getState().equals(OfferState.REJECTED)) {
            type = NotificationType.OFFER_REJECTED;
            receiver = offer.getOfferer();
        } else if (offer.getState().equals(OfferState.CANCELLED)) {
            type = NotificationType.OFFER_CANCELED;
            receiver = offer.getProduct().getSeller();
        } else {
            type = NotificationType.NEW_OFFER;
            receiver = offer.getProduct().getSeller();
        }

        Notification notification = Notification.builder()
                .receiver(receiver)
                .offerTarget(offer.getId())
                .type(type)
                .build();
        return createNotification(notification);
    }

    @Override
    public NotificationDTO notifyFollow(User userFollowed) {
        Notification notification = Notification.builder()
                .receiver(userFollowed)
                .type(NotificationType.FOLLOW)
                .userTarget(jwtContextUtils.getUserLoggedFromContext().getId())
                .build();
        return createNotification(notification);
    }

    @Override
    public NotificationDTO notifyMessage(User user) {
        Notification notification = Notification.builder()
                .receiver(user)
                .type(NotificationType.MESSAGE)
                .build();
        return createNotification(notification);
    }

    @Override
    public void notifyProductSold(Product product) {
        for(User user : product.getUsersThatLiked()){
            Notification notification = Notification.builder()
                    .receiver(user)
                    .productTarget(product.getId())
                    .type(NotificationType.PRODUCT_SOLD)
                    .build();
            createNotification(notification);
        }
    }

    @Override
    public void notifyNewProduct(Product product) {
        List<User> followers = product.getSeller().getFollowers().stream().map(Following::getFollower).toList();
        for(User user : followers){
            Notification notification = Notification.builder()
                    .receiver(user)
                    .productTarget(product.getId())
                    .type(NotificationType.NEW_PRODUCT)
                    .build();
            createNotification(notification);
        }
    }

    @Override
    public void notifyNewFavorite(Product product) {
        Notification notification = Notification.builder()
                .receiver(product.getSeller())
                .productTarget(product.getId())
                .type(NotificationType.NEW_FAVORITE)
                .build();
    }

    @Override
    public NotificationDTO readNotification(NotificationDTO notificationDTO) {
        Notification notification = notificationRepository.findById(notificationDTO.getId()).orElseThrow(() -> new IllegalArgumentException("Notification not found"));
        notification.setRead(true);
        Notification savedNotification = notificationRepository.save(notification);
        return mapToDTO(savedNotification);
    }

    @Override
    public Page<NotificationDTO> getAllUserNotifications(int page, int sizePage) {
        User user = jwtContextUtils.getUserLoggedFromContext();
        return notificationRepository.findAllByReceiver(user, PageRequest.of(page,sizePage)).map(this::mapToDTO);
    }


    Notification mapToEntity(NotificationDTO notificationDTO) {
        return modelMapper.map(notificationDTO, Notification.class);
    }

    NotificationDTO mapToDTO(Notification notification) {
        return modelMapper.map(notification, NotificationDTO.class);
    }
}
