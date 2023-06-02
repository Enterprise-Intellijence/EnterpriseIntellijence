package com.enterpriseintellijence.enterpriseintellijence.data.services;

import com.enterpriseintellijence.enterpriseintellijence.dto.ConversationDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.MessageDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.basics.ProductBasicDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.creation.MessageCreateDTO;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface MessageService {

    MessageDTO createMessage(MessageCreateDTO messageCreateDTO);
    MessageDTO replaceMessage(String id, MessageDTO messageDTO) throws IllegalAccessException;
    MessageDTO updateMessage (String id, MessageDTO patch) throws IllegalAccessException;
    void deleteMessage(String id) throws IllegalAccessException;
    MessageDTO getMessage(String id) throws IllegalAccessException;

    Page<MessageDTO> getConversation(String user, String prod, int page, int sizePage);

    Iterable<ConversationDTO> getAllMyConversations();

    void setReadMessages(List<String> idList);
}
