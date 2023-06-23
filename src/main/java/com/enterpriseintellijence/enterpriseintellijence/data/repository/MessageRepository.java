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

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, String>, JpaSpecificationExecutor<Message> {
    @Query("select p from Message p where ((p.sendUser.id=:user and p.receivedUser.id=:other) or (p.sendUser.id=:other and p.receivedUser.id=:user)) and p.product.id=:productId")
    Page<Message> findConversationWithProduct(@Param("user") String loggedUser,@Param("other") String otherUser,@Param("productId") String productId, Pageable pageable);

    @Query("select p from Message p where p.conversationId= :convId and (p.sendUser.id= :user or p.receivedUser.id= :user) order by p.messageDate Desc")
    Page<Message> findConversation(@Param("convId") String conversationId,@Param("user") String loggedUser , Pageable pageable);

    @Query("select CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Message p where ((p.sendUser.id= :user and p.receivedUser.id= :other) or (p.sendUser.id= :other and p.receivedUser.id= :user)) and p.conversationId=:convId")
    boolean checkValidConversationID(@Param("user") String loggedUser,@Param("other") String otherUser,@Param("convId") String conversationId);

    @Query("select CASE WHEN COUNT(p) = 0 THEN true ELSE false END FROM Message p where p.conversationId=:convId")
    boolean canUseConversationId(@Param("convId") String conversationId);

    @Query("select distinct p from Message p where (p.sendUser.id= :user or p.receivedUser.id= :user) AND p.messageDate = (SELECT MAX(m.messageDate) FROM Message m WHERE m.conversationId = p.conversationId) ")
    List<Message> findAllMyConversation(@Param("user") String loggedUser);



}
