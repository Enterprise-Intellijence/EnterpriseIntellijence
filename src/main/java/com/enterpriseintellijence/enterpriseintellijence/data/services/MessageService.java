package com.enterpriseintellijence.enterpriseintellijence.data.services;

import com.enterpriseintellijence.enterpriseintellijence.data.entities.Product;
import com.enterpriseintellijence.enterpriseintellijence.data.entities.User;
import com.enterpriseintellijence.enterpriseintellijence.dto.ConversationDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.MessageDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.basics.ProductBasicDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.creation.MessageCreateDTO;
import jakarta.annotation.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface MessageService {

    MessageDTO createMessage(MessageCreateDTO messageCreateDTO);
    MessageDTO replaceMessage(String id, MessageDTO messageDTO) throws IllegalAccessException;
    MessageDTO updateMessage (String id, MessageDTO patch) throws IllegalAccessException;
    void deleteMessage(String id) throws IllegalAccessException;
    MessageDTO getMessage(String id) throws IllegalAccessException;

    Page<MessageDTO> getConversation(String conversationId, int page, int sizePage);

    String getConversationId(User user1, User user2, @Nullable Product product);

    Iterable<ConversationDTO> getAllMyConversations();

    void setReadMessages(List<String> idList);
}
