package com.enterpriseintellijence.enterpriseintellijence.data.services;

import com.enterpriseintellijence.enterpriseintellijence.data.entities.Notification;
import com.enterpriseintellijence.enterpriseintellijence.data.entities.Offer;
import com.enterpriseintellijence.enterpriseintellijence.data.entities.User;
import com.enterpriseintellijence.enterpriseintellijence.data.repository.NotificationsRepository;
import com.enterpriseintellijence.enterpriseintellijence.data.repository.OfferRepository;
import com.enterpriseintellijence.enterpriseintellijence.data.repository.ProductRepository;
import com.enterpriseintellijence.enterpriseintellijence.data.repository.UserRepository;
import com.enterpriseintellijence.enterpriseintellijence.dto.NotificationDTO;
import com.enterpriseintellijence.enterpriseintellijence.security.JwtContextUtils;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class NotificationServiceImp implements NotificationService {

    private final NotificationsRepository notificationRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OfferRepository offerRepository;
    private final JwtContextUtils jwtContextUtils;
    private final ModelMapper modelMapper;
    @Override
    public NotificationDTO createNotification(NotificationDTO notificationDTO) {
        User receiver = userRepository.findById(notificationDTO.getReceiver().getId()).orElseThrow(() -> new IllegalArgumentException("Receiver not found"));
        if (notificationDTO.getType() == null) throw new IllegalArgumentException("Invalid notification");
        if (notificationDTO.getUserTarget() == null && notificationDTO.getProductTarget() == null && notificationDTO.getOfferTarget() == null) throw new IllegalArgumentException("Invalid notification");
        if (notificationDTO.getUserTarget() != null && userRepository.findById(notificationDTO.getUserTarget()).isEmpty())
            throw new IllegalArgumentException("User target not found");
        if (notificationDTO.getProductTarget() != null && productRepository.findById(notificationDTO.getProductTarget()).isEmpty())
            throw new IllegalArgumentException("Product target not found");
        if (notificationDTO.getOfferTarget() != null)
        {
            Offer offer = offerRepository.findById(notificationDTO.getOfferTarget()).orElseThrow(() -> new IllegalArgumentException("Offer target not found"));
            if(!receiver.equals(offer.getOfferer()) && !receiver.equals(offer.getProduct().getSeller()))
                throw new IllegalArgumentException("Invalid offer target");
        }
        notificationDTO.setDate(java.time.LocalDateTime.now());
        Notification notification = mapToEntity(notificationDTO);
        Notification savedNotification = notificationRepository.save(notification);
        return mapToDTO(savedNotification);
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
