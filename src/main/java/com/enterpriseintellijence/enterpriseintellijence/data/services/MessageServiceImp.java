package com.enterpriseintellijence.enterpriseintellijence.data.services;

import com.enterpriseintellijence.enterpriseintellijence.data.entities.Message;
import com.enterpriseintellijence.enterpriseintellijence.data.repository.MessageDAO;
import com.enterpriseintellijence.enterpriseintellijence.dto.MessageDTO;
import com.enterpriseintellijence.enterpriseintellijence.exception.IdMismatchException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MessageServiceImp implements MessageService{


    private final MessageDAO messageRepository;

    private final ModelMapper modelMapper;


    @Override
    public MessageDTO createMessage(MessageDTO messageDTO) {
        
        Message message = mapToEntity(messageDTO);
        message = messageRepository.save(message);

        return mapToDTO(message);
    }

    @Override
    public MessageDTO replaceMessage(String id, MessageDTO messageDTO) {

        throwOnIdMismatch(id, messageDTO);
        Message message = mapToEntity(messageDTO);
        message = messageRepository.save(message);

        return mapToDTO(message);
    }

    @Override
    public MessageDTO updateMessage(String id, MessageDTO messageDTO) {
        //TODO: Implement this method
        return null;
    }

    @Override
    public MessageDTO deleteMessage(String id) {
        Optional<Message> message = messageRepository.findById(id);

        if(!message.isPresent()) {
            throw new EntityNotFoundException("Message not found");
        }
        messageRepository.deleteById(id);

        return mapToDTO(message.get());
    }

    @Override
    public MessageDTO getMessage(String id) {
        Optional<Message> message = messageRepository.findById(id);

        if(!message.isPresent()) {
            throw new EntityNotFoundException("Message not found");
        }
        return mapToDTO(message.get());
    }

    // TODO: VA TESTATA ASSOLUTAMENTE
    private Iterable<MessageDTO> mapToDTO(Iterable<Message> messages) {
        Iterable<MessageDTO> messageDTOs = new ArrayList<>();
        modelMapper.map(messages,messageDTOs);
        return messageDTOs;
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
