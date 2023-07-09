package com.enterpriseintellijence.enterpriseintellijence.data.services;

import com.enterpriseintellijence.enterpriseintellijence.data.entities.Product;
import com.enterpriseintellijence.enterpriseintellijence.data.entities.User;
import com.enterpriseintellijence.enterpriseintellijence.dto.ConversationDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.MessageDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.creation.MessageCreateDTO;
import jakarta.annotation.Nullable;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface MessageService {

    MessageDTO createMessage(MessageCreateDTO messageCreateDTO);
    void deleteMessage(String id) throws IllegalAccessException;
    MessageDTO getMessage(String id) throws IllegalAccessException;

    Page<MessageDTO> getConversationMessages(String conversationId, int page, int sizePage);

    Optional<ConversationDTO> getConversation(User user1, User user2, @Nullable Product product);

    Optional<ConversationDTO> getConversation(String otherUserId, @Nullable String productId);
    Optional<ConversationDTO> getConversationById(String conversationId);

    Iterable<ConversationDTO> getAllMyConversations();

    void setReadMessages(List<String> idList);

}
