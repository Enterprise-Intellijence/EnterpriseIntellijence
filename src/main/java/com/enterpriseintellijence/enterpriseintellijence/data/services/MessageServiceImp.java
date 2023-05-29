package com.enterpriseintellijence.enterpriseintellijence.data.services;

import com.enterpriseintellijence.enterpriseintellijence.data.entities.Message;
import com.enterpriseintellijence.enterpriseintellijence.data.entities.Product;
import com.enterpriseintellijence.enterpriseintellijence.data.entities.User;
import com.enterpriseintellijence.enterpriseintellijence.data.repository.MessageRepository;
import com.enterpriseintellijence.enterpriseintellijence.data.repository.ProductRepository;
import com.enterpriseintellijence.enterpriseintellijence.data.repository.UserRepository;
import com.enterpriseintellijence.enterpriseintellijence.dto.MessageDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.UserDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.MessageStatus;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.UserRole;
import com.enterpriseintellijence.enterpriseintellijence.exception.IdMismatchException;

import com.enterpriseintellijence.enterpriseintellijence.security.JwtContextUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MessageServiceImp implements MessageService{


    private final MessageRepository messageRepository;
    private final JwtContextUtils jwtContextUtils;
    private final ProductRepository productRepository;

    private final UserRepository userRepository;
    private final UserService userService;
    private final Clock clock;

    private final ModelMapper modelMapper;

    @Override
    public MessageDTO createMessage(MessageDTO messageDTO) {
        Message message = mapToEntity(messageDTO);
        message.setId(null);
        User loggedUser = jwtContextUtils.getUserLoggedFromContext();
        // TODO: 23/05/2023 l'utente che invia lo prendo dal jwtcontext...dovrei confrontarlo con l'id del messaggioDTO.getSendUser?

        User receivedUser = userRepository.findById(messageDTO.getId()).orElseThrow(EntityNotFoundException::new) ;

        message.setSendUser(loggedUser);
        message.setReceivedUser(receivedUser);
        message.setMessageDate(LocalDateTime.now(clock));

        Product product;
        if(messageDTO.getProduct()!=null) {
            product = productRepository.findById(messageDTO.getProduct().getId()).orElseThrow(EntityNotFoundException::new);
            message.setProduct(product);
        }
        message.setMessageStatus(MessageStatus.UNREAD);
        message.setOffer(null);

        message = messageRepository.save(message);

        return mapToDTO(message);
    }

    @Override
    public MessageDTO replaceMessage(String id, MessageDTO messageDTO) throws IllegalAccessException {
        // TODO: 23/05/2023 ha senso che ci sia?

        return updateMessage(id,messageDTO);
    }


    @Override
    public MessageDTO updateMessage(String id, MessageDTO patch) throws IllegalAccessException {
        Message message = messageRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        throwOnIdMismatch(id,patch);
        User loggedUser =jwtContextUtils.getUserLoggedFromContext();
        if(loggedUser.getId().equals(message.getSendUser().getId()))
            throw new IllegalAccessException("Cannot modify message of others");

        if(message.getMessageStatus().equals(MessageStatus.READ))
            throw new IllegalAccessException("Cannot modify message readed");

        if (patch.getContext() != null) {
            message.setContext(patch.getContext());
        }

        if (patch.getReceivedUser() != null) {
            User receiver = userRepository.findById(patch.getId()).orElseThrow(EntityNotFoundException::new);
            message.setReceivedUser(receiver);
        }

        messageRepository.save(message);
        return mapToDTO(message);
    }

    @Override
    public void deleteMessage(String id) throws IllegalAccessException {
        Message message = messageRepository.findById(id).orElseThrow(EntityNotFoundException::new );
        User loggedUser = jwtContextUtils.getUserLoggedFromContext();

        if(!loggedUser.getRole().equals(UserRole.USER) && (loggedUser.getId().equals(message.getSendUser().getId()) || loggedUser.getId().equals(message.getReceivedUser().getId())))
            throw new IllegalAccessException("Cannot delete others message");

        if(message.getMessageStatus().equals(MessageStatus.READ) || message.getOffer()!=null)
            throw new IllegalAccessException("Cannot delete this message ");

        messageRepository.deleteById(id);
    }

    @Override
    public MessageDTO getMessage(String id) throws IllegalAccessException {
        Message message = messageRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        User loggedUser = jwtContextUtils.getUserLoggedFromContext();
        if(!loggedUser.getRole().equals(UserRole.USER) && (loggedUser.getId().equals(message.getSendUser().getId()) || loggedUser.getId().equals(message.getReceivedUser().getId())))
            throw new IllegalAccessException("Cannot read others message");

        return mapToDTO(message);
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
