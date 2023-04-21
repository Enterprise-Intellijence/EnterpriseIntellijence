package com.enterpriseintellijence.enterpriseintellijence.data.services;

import com.enterpriseintellijence.enterpriseintellijence.dto.MessageDTO;
import org.springframework.http.ResponseEntity;

public interface MessageService {

    public MessageDTO createMessage(MessageDTO messageDTO);
    public ResponseEntity<MessageDTO> replaceMessage(MessageDTO messageDTO);
    public ResponseEntity<MessageDTO> updateMessage (MessageDTO messageDTO);
    ResponseEntity<MessageDTO> deleteMessage(String id);
    ResponseEntity<MessageDTO> getMessage(String id);
}