package com.enterpriseintellijence.enterpriseintellijence.data.services;


import com.enterpriseintellijence.enterpriseintellijence.data.entities.Offer;
import com.enterpriseintellijence.enterpriseintellijence.data.entities.User;
import com.enterpriseintellijence.enterpriseintellijence.data.repository.OfferRepository;
import com.enterpriseintellijence.enterpriseintellijence.dto.OfferDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.UserDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.OfferState;
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
public class OfferServiceImpTest {

    private OfferServiceImp offerServiceImp;

    @Mock
    private OfferRepository offerRepository;

    @Mock
    private UserService userService;

    public ModelMapper modelMapper;

    private OfferDTO defaultOfferDTO;
    private Offer defaultOffer;

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

        defaultOffer = Offer.builder()
                .id("1")
                .amount(10.0F)
                .state(OfferState.PENDING)
                .offerer(defaultUserEntity)
                .build();

        defaultOfferDTO = modelMapper.map(defaultOffer, OfferDTO.class);

        offerServiceImp = new OfferServiceImp(modelMapper, userService, offerRepository);
    }

    @Test
    void whenMappingOfferEntityAndOfferDTO_thenCorrect() {

        /*OfferDTO offerDTO = OfferDTO.builder()
                .amount(10.0F)
                .state(OfferState.PENDING)
                .build();

        Offer offer = mapToEntity(offerDTO);

        Offer expectedOffer = Offer.builder()
                .amount(10.0F)
                .state(OfferState.PENDING)
                .build();

        assertThat(offer).usingRecursiveComparison().isEqualTo(expectedOffer);*/
    }


    @Test
    void whenSavingOfferDTO_thenSaveOffer() {

        var offerToSaveEntity = Offer.builder()
                .id("1")
                .offerer(defaultUserEntity)
                .amount(10.0F)
                .state(OfferState.PENDING)
                .build();

        when(userService.findUserFromContext()).thenReturn(Optional.of(defaultUserDTO));
        when(offerRepository.save(offerToSaveEntity)).thenReturn(defaultOffer);

        OfferDTO savedOffer = offerServiceImp.createOffer(defaultOfferDTO);

        assertThat(savedOffer).usingRecursiveComparison().isEqualTo(mapToDTO(defaultOffer));
    }


    @Test
    void whenReplacingOfferDTO_throwOnIdMismatch() {
        OfferDTO newOffer = OfferDTO.builder()
                .id("NOT 1")
                .offerer(defaultUserDTO)
                .state(OfferState.PENDING)
                .build();


//        when(offerRepository.findById("1")).thenReturn(Optional.of(defaultOffer));

        Assertions.assertThrows(IdMismatchException.class, () -> {
            offerServiceImp.replaceOffer("1", newOffer);
        });
    }

    @Test
    void whenReplacingOfferDTO_throwOnOfferNotFound() {
        OfferDTO offerToReplace = OfferDTO.builder()
                .id("1")
                .offerer(defaultUserDTO)
                .state(OfferState.PENDING)
                .build();

        when(offerRepository.findById("1")).thenReturn(Optional.empty());

        Assertions.assertThrows(EntityNotFoundException.class, () -> {
            offerServiceImp.replaceOffer("1", offerToReplace);
        });
    }


    @Test
    void whenRequestingUserDifferentFromNewOfferUser_thenThrow() {
        UserDTO differentUserDTO = UserDTO.builder()
                .id("2")
                .username("anotherUsername")
                .password("password")
                .email("another@email.com")
                .role(UserRole.USER)
                .provider(Provider.LOCAL)
                .build();

        OfferDTO newOffer = OfferDTO.builder()
                .id("1")
                .offerer(differentUserDTO)
                .state(OfferState.PENDING)
                .build();

        when(offerRepository.findById("1")).thenReturn(Optional.of(defaultOffer));
        when(userService.findUserFromContext()).thenReturn(Optional.of(defaultUserDTO));

        Assertions.assertThrows(IllegalAccessException.class, () -> {
            offerServiceImp.replaceOffer("1", newOffer);
        });
    }

    @Test
    void whenRequestingUserDifferentFromOldOfferUser_thenThrow() {
        UserDTO anotherUserDTO = UserDTO.builder()
                .id("2")
                .username("anotherUsername")
                .password("password")
                .email("another@email.com")
                .role(UserRole.USER)
                .provider(Provider.LOCAL)
                .build();

        when(userService.findUserFromContext()).thenReturn(Optional.of(anotherUserDTO));
        when(offerRepository.findById("1")).thenReturn(Optional.of(defaultOffer));

        Assertions.assertThrows(IllegalAccessException.class, () -> {
            offerServiceImp.replaceOffer("1", defaultOfferDTO);
        });
    }

    @Test
    void whenChangingState_thenThrow() {
        OfferDTO offerToReplace = OfferDTO.builder()
                .id("1")
                .offerer(defaultUserDTO)
                .state(OfferState.ACCEPTED)    // CHANGED STATE
                .build();

        when(offerRepository.findById("1")).thenReturn(Optional.of(defaultOffer));
        when(userService.findUserFromContext()).thenReturn(Optional.of(defaultUserDTO));

        Assertions.assertThrows(IllegalAccessException.class, () -> {
            offerServiceImp.replaceOffer("1", offerToReplace);
        });
    }


    @Test
    void whenReplacingOfferDTO_thenReplaceOffer() throws IllegalAccessException {
        /*OfferDTO offerToReplace = OfferDTO.builder()
                .id("1")
                .amount(10.0F)
                .offerer(defaultUserDTO)
                .state(OfferState.PENDING)
                .build();

        when(offerRepository.findById("1")).thenReturn(Optional.of(defaultOffer));
        when(offerRepository.save(defaultOffer)).thenReturn(defaultOffer);
        when(userService.findUserFromContext()).thenReturn(Optional.of(defaultUserDTO));

        OfferDTO replacedOffer = offerServiceImp.replaceOffer("1", offerToReplace);*/
    }


    @Test
    void whenGetOfferWithDifferentUser_thenThrow() {
        UserDTO anotherUserDTO = UserDTO.builder()
                .id("2")
                .username("anotherUsername")
                .password("password")
                .email("another@email.com")
                .build();

        when(userService.findUserFromContext()).thenReturn(Optional.of(anotherUserDTO));
        when(offerRepository.findById("1")).thenReturn(Optional.of(defaultOffer));

        Assertions.assertThrows(IllegalAccessException.class, () -> {
            offerServiceImp.getOffer("1");
        });
    }



    @Test
    void whenGetOfferById_thenCorrect() throws IllegalAccessException {
        when(offerRepository.findById("1")).thenReturn(Optional.of(defaultOffer));
        when(userService.findUserFromContext()).thenReturn(Optional.of(defaultUserDTO));

        OfferDTO foundOffer = offerServiceImp.getOffer("1");

        assertThat(foundOffer).usingRecursiveComparison().isEqualTo(defaultOfferDTO);
    }



    public Offer mapToEntity(OfferDTO offerDTO) {
        return modelMapper.map(offerDTO, Offer.class);
    }

    public OfferDTO mapToDTO(Offer offer) {
        return modelMapper.map(offer, OfferDTO.class);
    }
}
