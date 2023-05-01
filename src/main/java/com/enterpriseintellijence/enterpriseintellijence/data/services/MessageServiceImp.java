package com.enterpriseintellijence.enterpriseintellijence.data.services;

import com.enterpriseintellijence.enterpriseintellijence.data.entities.Message;
import com.enterpriseintellijence.enterpriseintellijence.data.entities.User;
import com.enterpriseintellijence.enterpriseintellijence.data.repository.MessageRepository;
import com.enterpriseintellijence.enterpriseintellijence.dto.MessageDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.UserDTO;
import com.enterpriseintellijence.enterpriseintellijence.exception.IdMismatchException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessageServiceImp implements MessageService{


    private final MessageRepository messageRepository;

    private final UserService userService;

    private final ModelMapper modelMapper;

    @Override
    public MessageDTO createMessage(MessageDTO messageDTO) {
        Message message = mapToEntity(messageDTO);

        UserDTO userDTO = userService.findUserFromContext()
                .orElseThrow(EntityNotFoundException::new);

        message.setSendUser(modelMapper.map(userDTO, User.class));

        message = messageRepository.save(message);

        return mapToDTO(message);
    }

    @Override
    public MessageDTO replaceMessage(String id, MessageDTO messageDTO) throws IllegalAccessException {
        throwOnIdMismatch(id, messageDTO);

        Message oldMessage = messageRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        Message newMessage = mapToEntity(messageDTO);

        Message message = mapToEntity(messageDTO);

        UserDTO requestingUser = userService.findUserFromContext()
                .orElseThrow(EntityNotFoundException::new);

        if(!requestingUser.getId().equals(oldMessage.getSendUser().getId())) {
            throw new IllegalAccessException("User cannot change order");
        }
        if(!requestingUser.getId().equals(newMessage.getSendUser().getId())) {
            throw new IllegalAccessException("User cannot change order");
        }

        message = messageRepository.save(message);

        return mapToDTO(message);
    }


    @Override
    public MessageDTO updateMessage(String id, JsonPatch patch) throws JsonPatchException {
        MessageDTO message = mapToDTO(messageRepository.findById(id).orElseThrow(EntityNotFoundException::new));
        message = applyPatch(patch, mapToEntity(message));
        messageRepository.save(mapToEntity(message));
        return message;
    }

    @Override
    public MessageDTO deleteMessage(String id) {
        Message message = messageRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        messageRepository.deleteById(id);

        return mapToDTO(message);
    }

    @Override
    public MessageDTO getMessage(String id) throws IllegalAccessException {
        Message message = messageRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        MessageDTO messageDTO = mapToDTO(message);

        UserDTO userDTO = userService.findUserFromContext()
                .orElseThrow(EntityNotFoundException::new);

        if(!userDTO.getId().equals(messageDTO.getSendUser().getId())) {
            throw new IllegalAccessException("User cannot read other's messages");
        }
        return mapToDTO(message);
    }

    public MessageDTO applyPatch(JsonPatch patch, Message message) throws JsonPatchException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode patched = patch.apply(objectMapper.convertValue(message, JsonNode.class));

        return objectMapper.convertValue(patched, MessageDTO.class);
    }

    private Message mapToEntity(MessageDTO messageDTO) {
        return modelMapper.map(messageDTO,Message.class);
    }

    private MessageDTO mapToDTO(Message message) {
        return modelMapper.map(message,MessageDTO.class);
    }

    private void throwOnIdMismatch(String id, MessageDTO messageDTO) {
        if (messageDTO.getId() != null && !messageDTO.getId().equals(id)) {
            throw new IdMismatchException();
        }
    }
}
