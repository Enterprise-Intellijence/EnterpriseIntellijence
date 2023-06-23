package com.enterpriseintellijence.enterpriseintellijence.data.services;

import com.enterpriseintellijence.enterpriseintellijence.dto.NotificationDTO;
import org.springframework.data.domain.Page;

public interface NotificationService {
    NotificationDTO createNotification(NotificationDTO notificationDTO);

    NotificationDTO readNotification(NotificationDTO notificationDTO);

    Page<NotificationDTO> getAllUserNotifications(int page, int sizePage);
}
