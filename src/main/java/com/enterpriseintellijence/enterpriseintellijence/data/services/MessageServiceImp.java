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
        User receivedUser = userRepository.findById(messageDTO.getReceivedUser().getId()).orElseThrow(EntityNotFoundException::new);

        message.setSendUser(loggedUser);
        message.setReceivedUser(receivedUser);
        message.setMessageDate(LocalDateTime.now(clock));

        Product product;
        if(messageDTO.getProduct()!=null) {
            product = productRepository.findById(messageDTO.getProduct().getId()).orElseThrow(EntityNotFoundException::new);
            message.setProduct(product);
        }
        message.setMessageStatus(MessageStatus.UNREAD);

        message = messageRepository.save(message);
        System.out.println("a chi?");

        return mapToDTO(message);
    }

    @Override
    public MessageDTO replaceMessage(String id, MessageDTO messageDTO) throws IllegalAccessException {
        // TODO: 23/05/2023 ha senso che ci sia?
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
    public MessageDTO updateMessage(String id, MessageDTO patch) {
        // TODO: 23/05/2023 ha senso che ci sia?
        MessageDTO message = mapToDTO(messageRepository.findById(id).orElseThrow(EntityNotFoundException::new));

        if (patch.getContext() != null) {
            message.setContext(patch.getContext());
        }

        if (patch.getSendUser() != null) {
            message.setSendUser(patch.getSendUser());
        }

        if (patch.getReceivedUser() != null) {
            message.setReceivedUser(patch.getReceivedUser());
        }

        messageRepository.save(mapToEntity(message));
        return message;
    }

    @Override
    public void deleteMessage(String id) throws IllegalAccessException {
        Message message = messageRepository.findById(id).orElseThrow(EntityNotFoundException::new );
        User loggedUser = jwtContextUtils.getUserLoggedFromContext();

        if(!loggedUser.getRole().equals(UserRole.USER) && (loggedUser.getId().equals(message.getSendUser().getId()) || loggedUser.getId().equals(message.getReceivedUser().getId())))
            throw new IllegalAccessException("Cannot delete others message");

        if(message.getMessageStatus().equals(MessageStatus.READ) && message.getOffer()!=null)
            throw new IllegalAccessException("Cannot delete message with offers read");

        //if(message.ge)

        messageRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        messageRepository.deleteById(id);
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
