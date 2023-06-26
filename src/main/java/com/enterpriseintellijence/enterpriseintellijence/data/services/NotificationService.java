package com.enterpriseintellijence.enterpriseintellijence.data.services;

import com.enterpriseintellijence.enterpriseintellijence.data.entities.*;
import com.enterpriseintellijence.enterpriseintellijence.dto.NotificationDTO;
import org.springframework.data.domain.Page;

public interface NotificationService {

    NotificationDTO notifyReview(Review review);

    NotificationDTO notifyOffer(Offer offer);

    NotificationDTO notifyFollow(User userFollowed);

    NotificationDTO notifyMessage(User user);

    void notifyProductSold(Product product);

    void notifyNewProduct(Product product);

    void notifyNewFavorite(Product product);

    NotificationDTO readNotification(NotificationDTO notificationDTO);

    Page<NotificationDTO> getAllUserNotifications(int page, int sizePage);
}
