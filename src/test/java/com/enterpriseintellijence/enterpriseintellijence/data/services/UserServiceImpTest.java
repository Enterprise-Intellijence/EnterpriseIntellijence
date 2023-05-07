package com.enterpriseintellijence.enterpriseintellijence.data.services;


import com.enterpriseintellijence.enterpriseintellijence.data.entities.User;
import com.enterpriseintellijence.enterpriseintellijence.data.repository.PaymentMethodRepository;
import com.enterpriseintellijence.enterpriseintellijence.data.repository.UserRepository;
import com.enterpriseintellijence.enterpriseintellijence.dto.UserDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.Provider;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.UserRole;
import com.enterpriseintellijence.enterpriseintellijence.exception.IdMismatchException;
import com.enterpriseintellijence.enterpriseintellijence.security.JwtContextUtils;

import jakarta.persistence.EntityNotFoundException;

import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class UserServiceImpTest {


    private UserServiceImp userServiceImp;
    @Mock
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PaymentMethodRepository paymentMethodRepository;

    public JwtContextUtils defaultContextUtil = new JwtContextUtils();
    public ModelMapper modelMapper;
    private UserDTO defaultUserDTO;
    private User defaultUserEntity;


    @BeforeEach
    public void setUp() {

        modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setFieldMatchingEnabled(true).setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE).setMatchingStrategy(MatchingStrategies.STRICT).setAmbiguityIgnored(true);
        defaultUserDTO = UserDTO.builder()
            .id("1")
            .username("username")
            .password("password")
            .email("email@gmail.com")
            .role(UserRole.USER)
            .provider(Provider.LOCAL)
            .build();

        defaultUserEntity = modelMapper.map(defaultUserDTO, User.class);

        userServiceImp = new UserServiceImp(userRepository, modelMapper, defaultContextUtil, paymentMethodRepository);
    }

    @Test
    void whenMappingUserEntityAndUserDTO_thenCorrect() {
        UserDTO userDTO = UserDTO.builder()
            .id("1")
            .username("checco")
            .password("ciao")
            .email("ciao@gmail.com")
            .role(UserRole.USER)
            .provider(Provider.LOCAL)
            .build();
        User user = mapToEntity(userDTO);
        User expectedUser = User.builder()
            .id("1")
            .username("checco")
            .password("ciao")
            .email("ciao@gmail.com")
            .role(UserRole.USER)
            .provider(Provider.LOCAL)
            .build();
        assertThat(user).usingRecursiveComparison().isEqualTo(expectedUser);
    }

    @Test
    void whenSavingUserDTO_thenSaveUser() {
        var userToSave = UserDTO.builder()
            .id("1")
            .username("checco")
            .password("ciao")
            .email("ciao@gmail.com")
            .role(UserRole.USER)
            .provider(Provider.LOCAL)
            .build();
        var userToSaveEntity = User.builder()
            .id("1")
            .username("checco")
            .password("ciao")
            .email("ciao@gmail.com")
            .role(UserRole.USER)
            .provider(Provider.LOCAL)
            .build();

        when(userRepository.save(userToSaveEntity)).thenReturn(defaultUserEntity);
        UserDTO savedUser = userServiceImp.createUser(userToSave);
        assertThat(savedUser).usingRecursiveComparison().isEqualTo(mapToDTO(defaultUserEntity));
    }

    @Test
    void whenReplacingUserDTO_throwOnIdMismatch() {
        UserDTO newUser = UserDTO.builder()
            .id("NOT 1")
            .username("checco")
            .password("ciao")
            .email("ciao@gmail.com")
            .role(UserRole.USER)
            .provider(Provider.LOCAL)
            .build();

        Assertions.assertThrows(IdMismatchException.class, () -> {
            userServiceImp.replaceUser("1", newUser);
        });
    }

    @Test
    void whenReplacingUserDTO_throwOnOrderNotFound() {
        UserDTO userToReplace = UserDTO.builder()
            .id("1")
            .username("checco")
            .password("ciao")
            .email("ciao@gmail.com")
            .role(UserRole.USER)
            .provider(Provider.LOCAL)
            .build();

        when(userRepository.findById("1")).thenReturn(Optional.empty());
        Assertions.assertThrows(EntityNotFoundException.class, () -> {
            userServiceImp.replaceUser("1", userToReplace);
        });
    }
    //todo: check se sono stati inseriti tutti i test!


    public UserDTO mapToDTO(User user) {
        return modelMapper.map(user, UserDTO.class);
    }

    public User mapToEntity(UserDTO userDTO) {
        return modelMapper.map(userDTO, User.class);
    }
}
