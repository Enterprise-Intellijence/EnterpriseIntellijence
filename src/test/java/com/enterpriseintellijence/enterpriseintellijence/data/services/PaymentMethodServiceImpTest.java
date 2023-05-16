package com.enterpriseintellijence.enterpriseintellijence.data.services;

import com.enterpriseintellijence.enterpriseintellijence.data.entities.PaymentMethod;
import com.enterpriseintellijence.enterpriseintellijence.data.entities.User;
import com.enterpriseintellijence.enterpriseintellijence.data.repository.PaymentMethodRepository;
import com.enterpriseintellijence.enterpriseintellijence.dto.PaymentMethodDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.UserDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.Provider;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.UserRole;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import com.enterpriseintellijence.enterpriseintellijence.exception.IdMismatchException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class PaymentMethodServiceImpTest {

    private PaymentMethodServiceImp paymentMethodServiceImp;
    @Mock
    private PaymentMethodRepository paymentMethodRepository;
    @Mock
    private UserService userService;
    public ModelMapper modelMapper;
    private PaymentMethodDTO defaultPaymentMethodDTO;
    private PaymentMethod defaultPaymentMethodEntity;
    private UserDTO defaultUserDTO;
    private User defaultUserEntity;

    @BeforeEach
    public void setUp() {
        modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setFieldMatchingEnabled(true).setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE).setMatchingStrategy(MatchingStrategies.STRICT).setAmbiguityIgnored(true);

        paymentMethodServiceImp = new PaymentMethodServiceImp(paymentMethodRepository, modelMapper, userService);

        defaultUserDTO = UserDTO.builder()
                .id("1")
                .username("username")
                .password("password")
                .email("email@gmail.com")
                .role(UserRole.USER)
                .provider(Provider.LOCAL)
                .build();

        defaultUserEntity = modelMapper.map(defaultUserDTO, User.class);

        defaultPaymentMethodEntity = PaymentMethod.builder()
                .id("1")
                .creditCard("123456789")
                .owner("owner")
                .expiryDate("expiryDate")
                .ownerUser(defaultUserEntity)
                .defaultUser(defaultUserEntity)
                .build();

        defaultPaymentMethodDTO = modelMapper.map(defaultPaymentMethodEntity, PaymentMethodDTO.class);
    }

    @Test
    void whenMappingPaymentMethodEntityAndPaymentMethodDTO_theCorrect() {
        PaymentMethodDTO paymentMethodDTO = PaymentMethodDTO.builder()
                .id("1")
                .creditCard("123456789")
                .owner("owner")
                .expiryDate("expiryDate")
                .ownerUser(defaultUserDTO)
                .defaultUser(defaultUserDTO)
                .build();

        PaymentMethod p = mapToEntity(paymentMethodDTO);

        PaymentMethod expectedPayment = PaymentMethod.builder()
                .id("1")
                .creditCard("123456789")
                .owner("owner")
                .expiryDate("expiryDate")
                .ownerUser(defaultUserEntity)
                .defaultUser(defaultUserEntity)
                .build();

        assertThat(p).usingRecursiveComparison().isEqualTo(expectedPayment);

    }

    @Test
    void whenMappingPaymentMethodDTOAndPaymentMethodEntity_theCorrect() {
        PaymentMethod paymentMethod = PaymentMethod.builder()
                .id("1")
                .creditCard("123456789")
                .owner("owner")
                .expiryDate("expiryDate")
                .ownerUser(defaultUserEntity)
                .defaultUser(defaultUserEntity)
                .build();

        PaymentMethodDTO p = mapToDTO(paymentMethod);

        PaymentMethodDTO expectedPayment = PaymentMethodDTO.builder()
                .id("1")
                .creditCard("123456789")
                .owner("owner")
                .expiryDate("expiryDate")
                .ownerUser(defaultUserDTO)
                .defaultUser(defaultUserDTO)
                .build();

        assertThat(p).usingRecursiveComparison().isEqualTo(expectedPayment);

    }

    @Test
    void whenSavingPaymentMethodDTO_thenSavePaymentMethod() throws IllegalAccessException {
       PaymentMethodDTO paymentMethodToSave = PaymentMethodDTO.builder()
                .id("1")
                .creditCard("123456789")
                .owner("owner")
                .expiryDate("expiryDate")
                .ownerUser(defaultUserDTO)
                .defaultUser(defaultUserDTO)
                .build();

       PaymentMethod paymentMethodToSaveEntity = PaymentMethod.builder()
                .id("1")
                .creditCard("123456789")
                .owner("owner")
                .expiryDate("expiryDate")
                .ownerUser(defaultUserEntity)
                .defaultUser(defaultUserEntity)
                .build();

       when(paymentMethodRepository.save(paymentMethodToSaveEntity)).thenReturn(paymentMethodToSaveEntity);
       when(paymentMethodRepository.save(mapToEntity(paymentMethodToSave))).thenReturn(paymentMethodToSaveEntity);
       when(userService.findUserFromContext()).thenReturn(Optional.of(defaultUserDTO));

       PaymentMethodDTO savedPaymentMethod = paymentMethodServiceImp.createPaymentMethod(paymentMethodToSave);
       assertThat(savedPaymentMethod).usingRecursiveComparison().isEqualTo(paymentMethodToSave);
    }

    @Test
    void whenReplacingPaymentMethodDTO_throwOnIdMismatch() {
        PaymentMethodDTO paymentMethodDTO = PaymentMethodDTO.builder()
                .id("NOT 1")
                .creditCard("123456789")
                .owner("owner")
                .expiryDate("expiryDate")
                .ownerUser(defaultUserDTO)
                .defaultUser(defaultUserDTO)
                .build();

        Assertions.assertThrows(IdMismatchException.class, () -> {
            paymentMethodServiceImp.replacePaymentMethod("1", paymentMethodDTO);
        });
    }

    @Test
    void whenReplacingPaymentMethodDTO_thenReplacePaymentMethod() throws IllegalAccessException {
        PaymentMethodDTO paymentMethodDTO = PaymentMethodDTO.builder()
                .id("1")
                .creditCard("123456789")
                .owner("owner")
                .expiryDate("expiryDate")
                .ownerUser(defaultUserDTO)
                .defaultUser(defaultUserDTO)
                .build();

        when(paymentMethodRepository.findById("1")).thenReturn(java.util.Optional.ofNullable(defaultPaymentMethodEntity));
        when(paymentMethodRepository.save(defaultPaymentMethodEntity)).thenReturn(defaultPaymentMethodEntity);
        when(userService.findUserFromContext()).thenReturn(Optional.of(defaultUserDTO));

        PaymentMethodDTO savedPaymentMethod = paymentMethodServiceImp.replacePaymentMethod("1", paymentMethodDTO);
        assertThat(savedPaymentMethod).usingRecursiveComparison().isEqualTo(paymentMethodDTO);
    }

    public PaymentMethod mapToEntity(PaymentMethodDTO paymentMethodDTO) {
        return modelMapper.map(paymentMethodDTO, PaymentMethod.class);
    }

    public PaymentMethodDTO mapToDTO(PaymentMethod paymentMethod) {
        return modelMapper.map(paymentMethod, PaymentMethodDTO.class);
    }
}
