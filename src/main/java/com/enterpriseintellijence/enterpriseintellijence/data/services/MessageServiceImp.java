package com.enterpriseintellijence.enterpriseintellijence.data.services;

import com.enterpriseintellijence.enterpriseintellijence.data.entities.Message;
import com.enterpriseintellijence.enterpriseintellijence.data.entities.Product;
import com.enterpriseintellijence.enterpriseintellijence.data.entities.User;
import com.enterpriseintellijence.enterpriseintellijence.data.repository.MessageRepository;
import com.enterpriseintellijence.enterpriseintellijence.data.repository.ProductRepository;
import com.enterpriseintellijence.enterpriseintellijence.data.repository.UserRepository;
import com.enterpriseintellijence.enterpriseintellijence.dto.ConversationDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.MessageDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.basics.ProductBasicDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.basics.UserBasicDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.creation.MessageCreateDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.MessageStatus;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.UserRole;
import com.enterpriseintellijence.enterpriseintellijence.exception.IdMismatchException;

import com.enterpriseintellijence.enterpriseintellijence.security.JwtContextUtils;
import jakarta.annotation.Nullable;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageServiceImp implements MessageService {


    private final MessageRepository messageRepository;
    private final JwtContextUtils jwtContextUtils;
    private final ProductRepository productRepository;
    private final NotificationService notificationService;
    private final UserRepository userRepository;
    private final UserService userService;
    private final Clock clock;

    private final ModelMapper modelMapper;

    @Override
    public MessageDTO createMessage(MessageCreateDTO messageCreateDTO) {
        Message message = new Message();
        User loggedUser = jwtContextUtils.getUserLoggedFromContext();
        User receivedUser = userRepository.findById(messageCreateDTO.getReceivedUser().getId()).orElseThrow(EntityNotFoundException::new);

        //controllo che il conversationId sia quello effettivamente associato alla conversazione tra le due persone
        if (messageCreateDTO.getConversationId() != null
            && messageRepository.checkValidConversationID(
            loggedUser.getId(),
            receivedUser.getId(),
            messageCreateDTO.getConversationId())) {
            message.setConversationId(messageCreateDTO.getConversationId());
        } else {
            //altrimenti lo genero nell'else e controllo di poterlo usare
            String convID = UUID.randomUUID().toString();
            while (!messageRepository.canUseConversationId(convID))
                convID = UUID.randomUUID().toString();
            message.setConversationId(convID);
        }

        message.setText(messageCreateDTO.getText());
        message.setMessageDate(LocalDateTime.now(clock));
        message.setMessageStatus(MessageStatus.UNREAD);
        message.setOffer(null);
        Product product;
        if (messageCreateDTO.getProduct() != null) {
            product = productRepository.findById(messageCreateDTO.getProduct().getId()).orElseThrow(EntityNotFoundException::new);
            message.setProduct(product);
        }

        message.setSendUser(loggedUser);
        message.setReceivedUser(receivedUser);
        message = messageRepository.save(message);

        notificationService.notifyMessage(message.getReceivedUser());

        return mapToDTO(message);
    }

    /*
    @Override
    public MessageDTO replaceMessage(String id, MessageDTO messageDTO) throws IllegalAccessException {
        // TODO: 23/05/2023 ha senso che ci sia?

        return updateMessage(id, messageDTO);
    }


    @Override
    public MessageDTO updateMessage(String id, MessageDTO patch) throws IllegalAccessException {
        Message message = messageRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        throwOnIdMismatch(id, patch);
        User loggedUser = jwtContextUtils.getUserLoggedFromContext();
        if (loggedUser.getId().equals(message.getSendUser().getId()))
            throw new IllegalAccessException("Cannot modify message of others");

        if (message.getMessageStatus().equals(MessageStatus.READ))
            throw new IllegalAccessException("Cannot modify message readed");

        message.setText(patch.getText());

        User receiver = userRepository.findById(patch.getId()).orElseThrow(EntityNotFoundException::new);
        message.setReceivedUser(receiver);

        messageRepository.save(message);
        return mapToDTO(message);
    }
    */

    @Override
    public void deleteMessage(String id) throws IllegalAccessException {
        // TODO: 04/06/2023 verificare la logica
        Message message = messageRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        User loggedUser = jwtContextUtils.getUserLoggedFromContext();

        if (!loggedUser.getRole().equals(UserRole.USER) && (loggedUser.getId().equals(message.getSendUser().getId()) || loggedUser.getId().equals(message.getReceivedUser().getId())))
            throw new IllegalAccessException("Cannot delete others message");

        if (message.getMessageStatus().equals(MessageStatus.READ) || message.getOffer() != null)
            throw new IllegalAccessException("Cannot delete this message ");

        messageRepository.deleteById(id);
    }

    @Override
    public MessageDTO getMessage(String id) throws IllegalAccessException {
        Message message = messageRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        User loggedUser = jwtContextUtils.getUserLoggedFromContext();
        if (!loggedUser.getRole().equals(UserRole.USER) && (loggedUser.getId().equals(message.getSendUser().getId()) || loggedUser.getId().equals(message.getReceivedUser().getId())))
            throw new IllegalAccessException("Cannot read others message");

        return mapToDTO(message);
    }

    @Override
    public Page<MessageDTO> getConversationMessages(String conversationId, int page, int sizePage) {
        try {
            User loggedUser = jwtContextUtils.getUserLoggedFromContext();
            Page<Message> messages = messageRepository.findConversation(conversationId, loggedUser.getId(), PageRequest.of(page, sizePage));
            if (messages == null || messages.isEmpty())
                throw new IllegalArgumentException("Wrong conversation Id. L'utente può leggere solo le sue conversazioni");

            List<MessageDTO> collect = messages.stream().map(s -> modelMapper.map(s, MessageDTO.class)).collect(Collectors.toList());

            return new PageImpl<>(collect, PageRequest.of(page, sizePage), messages.getTotalElements());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Optional<ConversationDTO> getConversation(User loggedUser, User otherUser, @Nullable Product product) {
        // TODO: 22/06/2023 why?? senza la query?

        if(loggedUser != jwtContextUtils.getUserLoggedFromContext())
            throw new IllegalArgumentException("Logged user is not the same of the user passed");


        List<Message> messages = messageRepository.findAllMyConversation(loggedUser.getId());

        for (Message message : messages) {
            if ((message.getSendUser().equals(loggedUser) && message.getReceivedUser().equals(otherUser)) ||
                (message.getSendUser().equals(otherUser) && message.getReceivedUser().equals(loggedUser)))
                if (product == null && message.getProduct() == null)
                    return Optional.of(convertToConversationDTO(message, loggedUser));
                else if (message.getProduct() != null && message.getProduct().equals(product))
                    return Optional.of(convertToConversationDTO(message, loggedUser));
        }

        return Optional.empty();
    }


    @Override
    public Optional<ConversationDTO> getConversation(String otherUserId, @Nullable String productId) {
        User loggedUser = jwtContextUtils.getUserLoggedFromContext();
        Optional<User> otherUser = userRepository.findById(otherUserId);
        Product product = null;

        if (otherUser.isEmpty())
            return Optional.empty();

        if (productId != null) {
            var productOptional = productRepository.findById(productId);
            if (productOptional.isEmpty())
                return Optional.empty();
            product = productOptional.get();
        }
        return getConversation(loggedUser, otherUser.get(), product);
    }

    @Override
    public Optional<ConversationDTO> getConversationById(String conversationId) {
        User loggedUser = jwtContextUtils.getUserLoggedFromContext();

        List<Message> messages = messageRepository.findAllMyConversation(loggedUser.getId());

        for (Message message : messages) {
            if (message.getId().equals(conversationId))
                return Optional.of(convertToConversationDTO(message, loggedUser));
        }
        return Optional.empty();
    }


    @Override
    public Iterable<ConversationDTO> getAllMyConversations() {

        User loggedUser = jwtContextUtils.getUserLoggedFromContext();
        try {
            List<Message> messages = messageRepository.findAllMyConversation(loggedUser.getId());

            return convertToConversationDTO(messages, loggedUser);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

//        return new ArrayList<ConversationDTO>(myConversationsMap.values());
    }

    @Override
    public void setReadMessages(List<String> idList) {
        for (String id : idList) {
            Optional<Message> message = messageRepository.findById(id);
            if (message.isPresent() && message.get().getReceivedUser().equals(jwtContextUtils.getUserLoggedFromContext())) {
                message.get().setMessageStatus(MessageStatus.READ);
                messageRepository.save(message.get());
            }

        }
    }


    private ConversationDTO convertToConversationDTO(Message message, User loggedUser) {
        UserBasicDTO userBasicDTO;
        if (message.getSendUser().equals(loggedUser))
            userBasicDTO = modelMapper.map(message.getReceivedUser(), UserBasicDTO.class);
        else
            userBasicDTO = modelMapper.map(message.getSendUser(), UserBasicDTO.class);

        ProductBasicDTO productBasicDTO = null;
        if (message.getProduct() != null) {
            productBasicDTO = modelMapper.map(message.getProduct(), ProductBasicDTO.class);
        }

        boolean isUnread = message.getReceivedUser().equals(loggedUser) && message.getMessageStatus().equals(MessageStatus.UNREAD);


        return ConversationDTO.builder()
            .otherUser(userBasicDTO)
            .lastMessage(modelMapper.map(message, MessageDTO.class))
            .productBasicDTO(productBasicDTO)
            .unreadMessages(isUnread)
            .conversationId(message.getConversationId())
            .build();
    }

    private List<ConversationDTO> convertToConversationDTO(List<Message> messages, User loggedUser) {
        List<ConversationDTO> conversationDTOS = new ArrayList<>();
        for (Message message : messages) {
            conversationDTOS.add(convertToConversationDTO(message, loggedUser));
        }
        return conversationDTOS;
    }

    private Message mapToEntity(MessageDTO messageDTO) {
        return modelMapper.map(messageDTO, Message.class);
    }

    private MessageDTO mapToDTO(Message message) {
        return modelMapper.map(message, MessageDTO.class);
    }

    private void throwOnIdMismatch(String id, MessageDTO messageDTO) {
        if (messageDTO.getId() != null && !messageDTO.getId().equals(id)) {
            throw new IdMismatchException();
        }
    }
}
