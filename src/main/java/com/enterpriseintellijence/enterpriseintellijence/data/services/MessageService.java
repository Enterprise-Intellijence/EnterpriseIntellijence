package com.enterpriseintellijence.enterpriseintellijence.data.services;

import com.enterpriseintellijence.enterpriseintellijence.dto.MessageDTO;

public interface MessageService {

    MessageDTO createMessage(MessageDTO messageDTO);
    MessageDTO replaceMessage(String id, MessageDTO messageDTO) throws IllegalAccessException;
    MessageDTO updateMessage (String id, MessageDTO patch);
    void deleteMessage(String id);
    MessageDTO getMessage(String id) throws IllegalAccessException;
}
