package com.enterpriseintellijence.enterpriseintellijence.data.services;

import com.enterpriseintellijence.enterpriseintellijence.data.entities.Message;
import com.enterpriseintellijence.enterpriseintellijence.data.entities.User;
import com.enterpriseintellijence.enterpriseintellijence.data.repository.MessageRepository;
import com.enterpriseintellijence.enterpriseintellijence.dto.MessageDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.UserFullDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.Provider;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.UserRole;
import com.enterpriseintellijence.enterpriseintellijence.exception.IdMismatchException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class MessageServiceImpTest {

    private MessageServiceImp messageServiceImp;

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private UserService userService;

    public ModelMapper modelMapper;

    private MessageDTO defaultMessageDTO;
    private Message defaultMessage;

    private UserFullDTO defaultUserFullDTO;
    private User defaultUserEntity;


    @BeforeEach
    public void setUp() {
        modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setFieldMatchingEnabled(true).setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE).setMatchingStrategy(MatchingStrategies.STRICT).setAmbiguityIgnored(true);

        defaultUserFullDTO = UserFullDTO.builder()
                .id("1")
                .username("username")
                .password("password")
                .email("email@gmail.com")
                .role(UserRole.USER)
                .provider(Provider.LOCAL)
                .build();

        defaultUserEntity = modelMapper.map(defaultUserFullDTO, User.class);

        defaultMessage = Message.builder()
                .id("1")
                .context("message")
                .sendUser(defaultUserEntity)
                .build();

        defaultMessageDTO = modelMapper.map(defaultMessage, MessageDTO.class);

        messageServiceImp = new MessageServiceImp(messageRepository, userService, modelMapper);
    }

    @Test
    void whenMappingMessageEntityAndMessageDTO_thenCorrect() {

        MessageDTO messageDTO = MessageDTO.builder()
                .id("1")
                .context("message")
                .sendUser(defaultUserFullDTO)
                .build();

        Message message = mapToEntity(messageDTO);

        Message expectedMessage = Message.builder()
                .id("1")
                .context("message")
                .sendUser(defaultUserEntity)
                .build();

        assertThat(message).usingRecursiveComparison().isEqualTo(expectedMessage);
    }


    @Test
    void whenSavingMessageDTO_thenSaveMessage() {

        var messageToSaveEntity = Message.builder()
                .id("1")
                .context("message")
                .sendUser(defaultUserEntity)
                .build();

        when(userService.findUserFromContext()).thenReturn(Optional.of(defaultUserFullDTO));
        when(messageRepository.save(messageToSaveEntity)).thenReturn(defaultMessage);

        MessageDTO savedMessage = messageServiceImp.createMessage(defaultMessageDTO);

        assertThat(savedMessage).usingRecursiveComparison().isEqualTo(mapToDTO(defaultMessage));
    }


    @Test
    void whenReplacingMessageDTO_throwOnIdMismatch() {
        MessageDTO newMessage = MessageDTO.builder()
                .id("NOT 1")
                .context("message")
                .sendUser(defaultUserFullDTO)
                .build();


//        when(messageRepository.findById("1")).thenReturn(Optional.of(defaultMessage));

        Assertions.assertThrows(IdMismatchException.class, () -> {
            messageServiceImp.replaceMessage("1", newMessage);
        });
    }

    @Test
    void whenReplacingMessageDTO_throwOnMessageNotFound() {
        MessageDTO MessageToReplace = MessageDTO.builder()
                .id("1")
                .context("message")
                .sendUser(defaultUserFullDTO)
                .build();

        when(messageRepository.findById("1")).thenReturn(Optional.empty());

        Assertions.assertThrows(EntityNotFoundException.class, () -> {
            messageServiceImp.replaceMessage("1", MessageToReplace);
        });
    }


    @Test
    void whenRequestingUserDifferentFromNewMessageUser_thenThrow() {
        UserFullDTO differentUserFullDTO = UserFullDTO.builder()
                .id("2")
                .username("anotherUsername")
                .password("password")
                .email("another@email.com")
                .role(UserRole.USER)
                .provider(Provider.LOCAL)
                .build();

        MessageDTO newMessage = MessageDTO.builder()
                .id("1")
                .context("message")
                .sendUser(differentUserFullDTO)
                .build();

        when(messageRepository.findById("1")).thenReturn(Optional.of(defaultMessage));
        when(userService.findUserFromContext()).thenReturn(Optional.of(defaultUserFullDTO));

        Assertions.assertThrows(IllegalAccessException.class, () -> {
            messageServiceImp.replaceMessage("1", newMessage);
        });
    }

    @Test
    void whenRequestingUserDifferentFromOldMessageUser_thenThrow() {
        UserFullDTO anotherUserFullDTO = UserFullDTO.builder()
                .id("2")
                .username("anotherUsername")
                .password("password")
                .email("another@email.com")
                .role(UserRole.USER)
                .provider(Provider.LOCAL)
                .build();

        when(userService.findUserFromContext()).thenReturn(Optional.of(anotherUserFullDTO));
        when(messageRepository.findById("1")).thenReturn(Optional.of(defaultMessage));

        Assertions.assertThrows(IllegalAccessException.class, () -> {
            messageServiceImp.replaceMessage("1", defaultMessageDTO);
        });
    }

    @Test
    void whenReplacingMessageDTO_thenReplaceMessage() throws IllegalAccessException {
        MessageDTO messageToReplace = MessageDTO.builder()
                .id("1")
                .context("message")
                .sendUser(defaultUserFullDTO)
                .build();

        when(messageRepository.findById("1")).thenReturn(Optional.of(defaultMessage));
        when(messageRepository.save(defaultMessage)).thenReturn(defaultMessage);
        when(userService.findUserFromContext()).thenReturn(Optional.of(defaultUserFullDTO));

        MessageDTO replacedMessage = messageServiceImp.replaceMessage("1", messageToReplace);
    }


    @Test
    void whenGetMessageWithDifferentUser_thenThrow() {
        UserFullDTO anotherUserFullDTO = UserFullDTO.builder()
                .id("2")
                .username("anotherUsername")
                .password("password")
                .email("another@email.com")
                .build();

        when(userService.findUserFromContext()).thenReturn(Optional.of(anotherUserFullDTO));
        when(messageRepository.findById("1")).thenReturn(Optional.of(defaultMessage));

        Assertions.assertThrows(IllegalAccessException.class, () -> {
            messageServiceImp.getMessage("1");
        });
    }



    @Test
    void whenGetMessageById_thenCorrect() throws IllegalAccessException {
        when(messageRepository.findById("1")).thenReturn(Optional.of(defaultMessage));
        when(userService.findUserFromContext()).thenReturn(Optional.of(defaultUserFullDTO));

        MessageDTO foundMessage = messageServiceImp.getMessage("1");

        assertThat(foundMessage).usingRecursiveComparison().isEqualTo(defaultMessageDTO);
    }



    public Message mapToEntity(MessageDTO MessageDTO) {
        return modelMapper.map(MessageDTO, Message.class);
    }

    public MessageDTO mapToDTO(Message Message) {
        return modelMapper.map(Message, MessageDTO.class);
    }

}
