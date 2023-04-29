package com.enterpriseintellijence.enterpriseintellijence.data.services;

import com.enterpriseintellijence.enterpriseintellijence.dto.MessageDTO;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import org.springframework.http.ResponseEntity;

public interface MessageService {

    public MessageDTO createMessage(MessageDTO messageDTO);
    public MessageDTO replaceMessage(String id, MessageDTO messageDTO) throws IllegalAccessException;
    public MessageDTO updateMessage (String id, JsonPatch patch) throws JsonPatchException;
    MessageDTO deleteMessage(String id);
    MessageDTO getMessage(String id) throws IllegalAccessException;
}
