package com.enterpriseintellijence.enterpriseintellijence.data.services;

import com.enterpriseintellijence.enterpriseintellijence.dto.MessageDTO;
import org.springframework.http.ResponseEntity;

public interface MessageService {

    public MessageDTO createMessage(MessageDTO messageDTO);
    public MessageDTO replaceMessage(String id, MessageDTO messageDTO);
    public MessageDTO updateMessage (String id, MessageDTO messageDTO);
    MessageDTO deleteMessage(String id);
    MessageDTO getMessage(String id);
}
