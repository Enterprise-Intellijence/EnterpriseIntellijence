package com.enterpriseintellijence.enterpriseintellijence.data.services;

import com.enterpriseintellijence.enterpriseintellijence.data.entities.Order;
import com.enterpriseintellijence.enterpriseintellijence.data.entities.User;
import com.enterpriseintellijence.enterpriseintellijence.data.repository.OrderRepository;
import com.enterpriseintellijence.enterpriseintellijence.dto.*;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.OrderState;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.Provider;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.UserRole;
import com.enterpriseintellijence.enterpriseintellijence.exception.IdMismatchException;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;


import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class OrderServiceImpTest {

    private OrderServiceImp orderServiceImp;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserService userService;

    public ModelMapper modelMapper;

    Clock fixedClock = Clock.fixed(Instant.parse("2022-12-21T10:15:30.00Z"), ZoneId.of("UTC"));

    private OrderDTO defaultOrderDTO;
    private Order defaultOrder;

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

        defaultOrder = Order.builder()
            .id("1")
            .orderDate(LocalDateTime.now(fixedClock))
            .state(OrderState.PURCHASED)
            .user(defaultUserEntity)
            .build();

        defaultOrderDTO = modelMapper.map(defaultOrder, OrderDTO.class);

        orderServiceImp = new OrderServiceImp(orderRepository, userService, modelMapper, fixedClock);

    }

    @Test
    void whenMappingOrderEntityAndOrderDTO_thenCorrect() {

        LocalDateTime dateTime = LocalDateTime.now(fixedClock);
        OrderDTO orderDTO = OrderDTO.builder()
            .orderDate(dateTime)
            .state(OrderState.PURCHASED)
            .build();

        Order order = mapToEntity(orderDTO);

        Order expectedOrder = Order.builder()
            .orderDate(dateTime)
            .state(OrderState.PURCHASED)
            .build();

        assertThat(order).usingRecursiveComparison().isEqualTo(expectedOrder);
    }


    @Test
    void whenSavingOrderDTO_thenSaveOrder() {
        var orderToSave = OrderCreateDTO.builder()
            .state(OrderState.PURCHASED)
            .build();

        var orderToSaveEntity = Order.builder()
            .user(defaultUserEntity)
            .orderDate(LocalDateTime.now(fixedClock))
            .state(OrderState.PURCHASED)
            .build();

        when(userService.findUserFromContext()).thenReturn(Optional.of(defaultUserDTO));
        when(orderRepository.save(orderToSaveEntity)).thenReturn(defaultOrder);

        OrderDTO savedOrder = orderServiceImp.createOrder(orderToSave);
        assertThat(savedOrder).usingRecursiveComparison().isEqualTo(mapToDTO(defaultOrder));
    }


    @Test
    void whenReplacingOrderDTO_throwOnIdMismatch() {
        OrderDTO newOrder = OrderDTO.builder()
            .id("NOT 1")
            .user(defaultUserDTO)
            .state(OrderState.PURCHASED)
            .build();


//        when(orderRepository.findById("1")).thenReturn(Optional.of(defaultOrder));

        Assertions.assertThrows(IdMismatchException.class, () -> {
            orderServiceImp.replaceOrder("1", newOrder);
        });
    }

    @Test
    void whenReplacingOrderDTO_throwOnOrderNotFound() {
        OrderDTO orderToReplace = OrderDTO.builder()
            .id("1")
            .user(defaultUserDTO)
            .state(OrderState.PURCHASED)
            .build();

        when(orderRepository.findById("1")).thenReturn(Optional.empty());

        Assertions.assertThrows(EntityNotFoundException.class, () -> {
            orderServiceImp.replaceOrder("1", orderToReplace);
        });
    }


    @Test
    void whenRequestingUserDifferentFromNewOrderUser_thenThrow() {
        UserDTO differentUserDTO = UserDTO.builder()
            .id("2")
            .username("anotherUsername")
            .password("password")
            .email("another@email.com")
            .role(UserRole.USER)
            .provider(Provider.LOCAL)
            .build();

        OrderDTO newOrder = OrderDTO.builder()
            .id("1")
            .user(differentUserDTO)
            .state(OrderState.PURCHASED)
            .build();

        when(orderRepository.findById("1")).thenReturn(Optional.of(defaultOrder));
        when(userService.findUserFromContext()).thenReturn(Optional.of(defaultUserDTO));

        Assertions.assertThrows(IllegalAccessException.class, () -> {
            orderServiceImp.replaceOrder("1", newOrder);
        });
    }

    @Test
    void whenRequestingUserDifferentFromOldOrderUser_thenThrow() {
        UserDTO anotherUserDTO = UserDTO.builder()
            .id("2")
            .username("anotherUsername")
            .password("password")
            .email("another@email.com")
            .role(UserRole.USER)
            .provider(Provider.LOCAL)
            .build();

        when(userService.findUserFromContext()).thenReturn(Optional.of(anotherUserDTO));
        when(orderRepository.findById("1")).thenReturn(Optional.of(defaultOrder));

        Assertions.assertThrows(IllegalAccessException.class, () -> {
            orderServiceImp.replaceOrder("1", defaultOrderDTO);
        });
    }

    @Test
    void whenChangingState_thenThrow() {
        OrderDTO orderToReplace = OrderDTO.builder()
            .id("1")
            .user(defaultUserDTO)
            .orderDate(LocalDateTime.now(fixedClock))
            .state(OrderState.COMPLETED)    // CHANGED STATE
            .build();

        when(orderRepository.findById("1")).thenReturn(Optional.of(defaultOrder));
        when(userService.findUserFromContext()).thenReturn(Optional.of(defaultUserDTO));

        Assertions.assertThrows(IllegalAccessException.class, () -> {
            orderServiceImp.replaceOrder("1", orderToReplace);
        });
    }

    @Test
    void whenReplacingOrderDTO_thenReplaceOrder() throws IllegalAccessException {
        OrderDTO orderToReplace = OrderDTO.builder()
            .id("1")
            .user(defaultUserDTO)
            .orderDate(LocalDateTime.now(fixedClock))
            .state(OrderState.PURCHASED)
            .build();


        when(orderRepository.findById("1")).thenReturn(Optional.of(defaultOrder));
        when(orderRepository.save(defaultOrder)).thenReturn(defaultOrder);
        when(userService.findUserFromContext()).thenReturn(Optional.of(defaultUserDTO));

        OrderDTO replacedOrder = orderServiceImp.replaceOrder("1", orderToReplace);
    }


    @Test
    void whenGetOrderWithDifferentUser_thenThrow() {
        UserDTO anotherUserDTO = UserDTO.builder()
            .id("2")
            .username("anotherUsername")
            .password("password")
            .email("another@email.com")
            .build();

        when(userService.findUserFromContext()).thenReturn(Optional.of(anotherUserDTO));
        when(orderRepository.findById("1")).thenReturn(Optional.of(defaultOrder));

        Assertions.assertThrows(IllegalAccessException.class, () -> {
            orderServiceImp.getOrderById("1");
        });
    }

    @Test
    void whenGetOrderWithSameUser_thenCorrect() throws IllegalAccessException {
        when(orderRepository.findById("1")).thenReturn(Optional.of(defaultOrder));
        when(userService.findUserFromContext()).thenReturn(Optional.of(defaultUserDTO));

        OrderDTO foundOrder = orderServiceImp.getOrderById("1");

        assertThat(foundOrder).usingRecursiveComparison().isEqualTo(defaultOrderDTO);
    }


    @Test
    void whenUpdatingStateFromPURCHASEDtoCANCELED_thenThrow() throws IllegalAccessException {
        OrderDTO order = OrderDTO.builder()
                .id("1")
                .user(defaultUserDTO)
                .orderDate(LocalDateTime.now(fixedClock))
                .state(OrderState.CANCELED)
                .build();

        when(orderRepository.findById("1")).thenReturn(Optional.of(mapToEntity(order)));
        when(userService.findUserFromContext()).thenReturn(Optional.of(defaultUserDTO));

        Assertions.assertThrows(IllegalAccessException.class, () -> {
            orderServiceImp.updateOrder("1", order);
        });

        assertThat(orderServiceImp.getOrderById("1")).usingRecursiveComparison().isEqualTo(order);
    }

    @Test
    void whenUpdatingStateFromDELIVEREDtoPENDING_thenThrow() throws IllegalAccessException {
        OrderDTO orderToReplace = OrderDTO.builder()
                .id("1")
                .user(defaultUserDTO)
                .orderDate(LocalDateTime.now(fixedClock))
                .state(OrderState.PENDING)
                .build();

        OrderDTO order = OrderDTO.builder()
                .id("1")
                .user(defaultUserDTO)
                .orderDate(LocalDateTime.now(fixedClock))
                .state(OrderState.PENDING)
                .build();

        when(orderRepository.findById("1")).thenReturn(Optional.of(mapToEntity(order)));
        when(userService.findUserFromContext()).thenReturn(Optional.of(defaultUserDTO));

        Assertions.assertThrows(IllegalAccessException.class, () -> {
            orderServiceImp.updateOrder("1", orderToReplace);
        });

        assertThat(orderServiceImp.getOrderById("1")).usingRecursiveComparison().isEqualTo(order);
    }



    public Order mapToEntity(OrderDTO orderDTO) {
        return modelMapper.map(orderDTO, Order.class);
    }

    public OrderDTO mapToDTO(Order order) {
        return modelMapper.map(order, OrderDTO.class);
    }
}
