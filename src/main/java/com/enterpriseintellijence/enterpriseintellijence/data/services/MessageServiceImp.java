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
public class MessageServiceImp implements MessageService{


    private final MessageRepository messageRepository;
    private final JwtContextUtils jwtContextUtils;
    private final ProductRepository productRepository;

    private final UserRepository userRepository;
    private final UserService userService;
    private final Clock clock;

    private final ModelMapper modelMapper;

    @Override
    public MessageDTO createMessage(MessageCreateDTO messageCreateDTO) {
        Message message = new Message();
        User loggedUser = jwtContextUtils.getUserLoggedFromContext();
        User receivedUser = userRepository.findById(messageCreateDTO.getReceivedUser().getId()).orElseThrow(EntityNotFoundException::new) ;

        //controllo che il conversationId sia quello effettivamente associato alla conversazione tra le due persone
        if(messageCreateDTO.getConversationId()!=null
                && messageRepository.checkValidConversationID(
                        loggedUser.getId(),
                        receivedUser.getId(),
                        messageCreateDTO.getConversationId()))
        {
            message.setConversationId(messageCreateDTO.getConversationId());
        }
        else{
          //altrimenti lo genero nell'else e controllo di poterlo usare
            String convID = UUID.randomUUID().toString();
            while(!messageRepository.canUseConversationId(convID))
                convID = UUID.randomUUID().toString();
            message.setConversationId(convID);
        }

        message.setText(messageCreateDTO.getText());
        message.setMessageDate(LocalDateTime.now(clock));
        message.setMessageStatus(MessageStatus.UNREAD);
        message.setOffer(null);
        Product product;
        if(messageCreateDTO.getProduct()!=null) {
            product = productRepository.findById(messageCreateDTO.getProduct().getId()).orElseThrow(EntityNotFoundException::new);
            message.setProduct(product);
        }

        message.setSendUser(loggedUser);
        message.setReceivedUser(receivedUser);
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

        if (patch.getText() != null) {
            message.setText(patch.getText());
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
        // TODO: 04/06/2023 verificare la logica
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

    @Override
    public Page<MessageDTO> getConversation(String conversationId, int page, int sizePage) {
        try {
            User loggedUser = jwtContextUtils.getUserLoggedFromContext();
            Page<Message> messages = messageRepository.findConversation(conversationId, loggedUser.getId(), PageRequest.of(page,sizePage));
            if(messages==null | messages.isEmpty())
                throw new IllegalArgumentException("Wrong conversation Id. L'utente può leggere solo le sue conversazioni");

            List<MessageDTO> collect=messages.stream().map(s->modelMapper.map(s,MessageDTO.class)).collect(Collectors.toList());

            return new PageImpl<>(collect, PageRequest.of(page,sizePage),messages.getTotalElements());
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String getConversationId(User user1, User user2, @Nullable Product product){
        // TODO: 22/06/2023 why?? senza la query? 
        List<Message> messages = messageRepository.findAllMyConversation(jwtContextUtils.getUserLoggedFromContext().getId());

        for(Message message : messages){
            if((message.getSendUser().equals(user1) && message.getReceivedUser().equals(user2)) ||
                    (message.getSendUser().equals(user2) && message.getReceivedUser().equals(user1)))
                if(message.getProduct()!=null && message.getProduct().equals(product))
                    return message.getConversationId();
        }
        String convID = UUID.randomUUID().toString();
        while(!messageRepository.canUseConversationId(convID))
            convID = UUID.randomUUID().toString();
        return convID;
    }
    @Override
    public Iterable<ConversationDTO> getAllMyConversations() {
/*
        Map<String,ConversationDTO> myConversationsMap = new HashMap<>();
        String loggedUserID= loggedUser.getId();

        for(Message message: loggedUser.getSentMessages()){
            String tempKey;
            if(message.getProduct()!=null){
                tempKey=message.getProduct().getId()+loggedUserID+message.getReceivedUser().getId();
            }else
                tempKey=loggedUserID+message.getReceivedUser().getId();
            checkAndSetConversation(myConversationsMap,tempKey,message,true);
        }

        for(Message message: loggedUser.getReceivedMessages()){
            String tempKey;
            if(message.getProduct()!=null){
                tempKey=message.getProduct().getId()+loggedUserID+message.getSendUser().getId();
            }else
                tempKey=loggedUserID+message.getSendUser().getId();
            checkAndSetConversation(myConversationsMap,tempKey,message,false);
        }
*/
        User loggedUser = jwtContextUtils.getUserLoggedFromContext();
        try{
            List<Message> messages = messageRepository.findAllMyConversation(loggedUser.getId());

            return convertToConversationDTO(messages,loggedUser);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

//        return new ArrayList<ConversationDTO>(myConversationsMap.values());
    }

    @Override
    public void setReadMessages(List<String> idList) {
        for (String id: idList){
            Optional<Message> message= messageRepository.findById(id);
            if(message.isPresent() && message!=null){
                message.get().setMessageStatus(MessageStatus.READ);
                messageRepository.save(message.get());
            }

        }
    }

/*    private void checkAndSetConversation(Map<String,ConversationDTO> myConversationsMap,String key,Message message,boolean sent){
        if(!myConversationsMap.containsKey(key)){
            ConversationDTO conversationDTO = new ConversationDTO();
            if(sent)
                conversationDTO.setOtherUser(modelMapper.map(message.getReceivedUser(), UserBasicDTO.class) );
            else
                conversationDTO.setOtherUser(modelMapper.map(message.getSendUser(), UserBasicDTO.class) );

            if(message.getProduct()!=null)
                conversationDTO.setProductBasicDTO(modelMapper.map(message.getProduct(), ProductBasicDTO.class) );
            conversationDTO.setLastMessage(modelMapper.map(message,MessageDTO.class));
            int n=0;
            if(message.getMessageStatus().equals(MessageStatus.UNREAD))
                n++;
            conversationDTO.setUnreadMessagesCount(n);
            myConversationsMap.put(key,conversationDTO);
        }
        else{
            if(myConversationsMap.get(key).getLastMessage().getMessageDate().isAfter(message.getMessageDate()))
                myConversationsMap.get(key).setLastMessage(modelMapper.map(message,MessageDTO.class));
            if(message.getMessageStatus().equals(MessageStatus.UNREAD))
                myConversationsMap.get(key).setUnreadMessagesCount(myConversationsMap.get(key).getUnreadMessagesCount()+1);
        }
    }*/

    private List<ConversationDTO> convertToConversationDTO(List<Message> messages, User loggedUser){
        List<ConversationDTO> conversationDTOS = new ArrayList<>();
        for (Message message: messages){
            UserBasicDTO userBasicDTO;
            if(message.getSendUser().equals(loggedUser))
                userBasicDTO = modelMapper.map(message.getReceivedUser(),UserBasicDTO.class);
            else
                userBasicDTO = modelMapper.map(message.getSendUser(),UserBasicDTO.class);

            ProductBasicDTO productBasicDTO = null;
            if (message.getProduct() != null) {
                productBasicDTO = modelMapper.map(message.getProduct(), ProductBasicDTO.class);
            }
            conversationDTOS.add(ConversationDTO.builder()
                    .otherUser(userBasicDTO)
                    .lastMessage(modelMapper.map(message,MessageDTO.class))
                    .productBasicDTO(productBasicDTO)
                    .unreadMessages(message.getMessageStatus().equals(MessageStatus.UNREAD))
                    .conversationId(message.getConversationId())
                    .build());
        }
        return conversationDTOS;
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
