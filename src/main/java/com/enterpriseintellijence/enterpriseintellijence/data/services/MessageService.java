package com.enterpriseintellijence.enterpriseintellijence.data.services;

import com.enterpriseintellijence.enterpriseintellijence.dto.MessageDTO;

public interface MessageService {

    public MessageDTO createMessage(MessageDTO messageDTO);
    public MessageDTO replaceMessage(String id, MessageDTO messageDTO) throws IllegalAccessException;
    public MessageDTO updateMessage (String id, MessageDTO patch);
    MessageDTO deleteMessage(String id);
    MessageDTO getMessage(String id) throws IllegalAccessException;
}
