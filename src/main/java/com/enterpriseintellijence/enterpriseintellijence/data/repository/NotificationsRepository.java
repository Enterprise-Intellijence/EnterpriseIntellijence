package com.enterpriseintellijence.enterpriseintellijence.data.repository;

import com.enterpriseintellijence.enterpriseintellijence.data.entities.Notification;
import com.enterpriseintellijence.enterpriseintellijence.data.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationsRepository extends JpaRepository<Notification, String>, JpaSpecificationExecutor<Notification> {

    Page<Notification> findAllByReceiver(User receiver, Pageable pageable);
    void deleteAllByReceiverAndReadIsTrue(User userLoggedFromContext);
}
