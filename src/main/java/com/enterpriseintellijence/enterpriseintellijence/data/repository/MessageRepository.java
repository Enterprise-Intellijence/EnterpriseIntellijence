package com.enterpriseintellijence.enterpriseintellijence.data.repository;

import com.enterpriseintellijence.enterpriseintellijence.data.entities.Message;
import com.enterpriseintellijence.enterpriseintellijence.data.entities.Product;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.Visibility;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MessageRepository extends JpaRepository<Message, String>, JpaSpecificationExecutor<Message> {
    @Query("select p from Message p where ((p.sendUser.id=:user and p.receivedUser.id=:other) or (p.sendUser.id=:other and p.receivedUser.id=:user)) and p.product.id=:productId")
    Page<Message> findConversationWithProduct(@Param("user") String loggedUser,@Param("other") String otherUser,@Param("productId") String productId, Pageable pageable);

    @Query("select p from Message p where ((p.sendUser.id=:user and p.receivedUser.id=:other) or (p.sendUser.id=:other and p.receivedUser.id=:user)) ")
    Page<Message> findConversation(@Param("user") String loggedUser,@Param("other") String otherUser, Pageable pageable);

}
