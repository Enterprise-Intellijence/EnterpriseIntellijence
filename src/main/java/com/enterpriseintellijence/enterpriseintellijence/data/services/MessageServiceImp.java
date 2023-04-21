package com.enterpriseintellijence.enterpriseintellijence.data.services;

import com.enterpriseintellijence.enterpriseintellijence.dto.MessageDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class MessageServiceImp implements MessageService{

    @Override
    public MessageDTO createMessage(MessageDTO messageDTO) {
        //TODO: Implement this method
        return null;
    }

    @Override
    public ResponseEntity<MessageDTO> replaceMessage(MessageDTO messageDTO) {
        //TODO: Implement this method
        return null;
    }

    @Override
    public ResponseEntity<MessageDTO> updateMessage(MessageDTO messageDTO) {
        //TODO: Implement this method
        return null;
    }

    @Override
    public ResponseEntity<MessageDTO> deleteMessage(String id) {
        //TODO: Implement this method
        return null;
    }

    @Override
    public ResponseEntity<MessageDTO> getMessage(String id) {
        //TODO: Implement this method
        return null;
    }
}
