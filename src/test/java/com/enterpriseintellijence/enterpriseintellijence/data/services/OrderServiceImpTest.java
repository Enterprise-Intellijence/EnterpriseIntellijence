package com.enterpriseintellijence.enterpriseintellijence.data.services;

import com.enterpriseintellijence.enterpriseintellijence.data.entities.*;
import com.enterpriseintellijence.enterpriseintellijence.data.entities.Order;
import com.enterpriseintellijence.enterpriseintellijence.data.repository.OrderRepository;
import com.enterpriseintellijence.enterpriseintellijence.dto.*;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.OrderState;
import com.enterpriseintellijence.enterpriseintellijence.exception.IdMismatchException;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.LOCAL_DATE;
import static org.mockito.Mockito.*;


import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class OrderServiceImpTest {

    private OrderServiceImp orderServiceImp;

    @Mock
    private OrderRepository orderRepository;

    public ModelMapper modelMapper;

    Clock fixedClock = Clock.fixed(Instant.parse("2022-12-21T10:15:30.00Z"), ZoneId.of("UTC"));

    @BeforeEach
    public void setUp() {
        modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setFieldMatchingEnabled(true).setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE).setMatchingStrategy(MatchingStrategies.STRICT).setAmbiguityIgnored(true);
        orderServiceImp = new OrderServiceImp(orderRepository, modelMapper, fixedClock);
    }

    // test corretto
    @Test
    void map() {

        LocalDateTime now = LocalDateTime.now();

        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setOrderDate(now);
        orderDTO.setState(OrderState.PURCHASED);
        orderDTO.setProduct(mock(ProductDTO.class));
        orderDTO.setDelivery(mock(DeliveryDTO.class));
        orderDTO.setOffer(mock(OfferDTO.class));
        orderDTO.setUser(mock(UserDTO.class));

        Product mockProduct = mock(Product.class);
        Delivery mockDelivery = mock(Delivery.class);
        Offer mockOffer = mock(Offer.class);
        User mockUser = mock(User.class);

        Order order = mapToEntity(orderDTO);
        order.setProduct(mockProduct);
        order.setDelivery(mockDelivery);
        order.setOffer(mockOffer);
        order.setUser(mockUser);

        Order expectedOrder = Order.builder()
            .orderDate(now)
            .state(OrderState.PURCHASED)
            .product(mockProduct)
            .delivery(mockDelivery)
            .offer(mockOffer)
            .user(mockUser)
            .build();


        assertThat(expectedOrder).usingRecursiveComparison().isEqualTo(order);
    }

    @Test
    void whenSavingOrderDTO_thenSaveOrder() {
        var orderToSave = OrderCreateDTO.builder()
            .state(OrderState.PURCHASED)
            .build();

        Order expectedToSave = Order.builder()
            .orderDate(LocalDateTime.now(fixedClock))
            .state(OrderState.PURCHASED)
            .build();

        Order expectedSaved = Order.builder()
            .id("1")
            .orderDate(LocalDateTime.now(fixedClock))
            .state(OrderState.PURCHASED)
            .build();


        var serviceImp = new OrderServiceImp(orderRepository, modelMapper, fixedClock);

        when(orderRepository.save(expectedToSave)).thenReturn(expectedSaved);

        OrderDTO savedOrder = serviceImp.createOrder(orderToSave);
        assertThat(savedOrder).usingRecursiveComparison().isEqualTo(mapToDTO(expectedSaved));
    }


    @Test
    void whenReplacingOrderDTO_throwOnIdMismatch() {
        OrderDTO orderToReplace = OrderDTO.builder()
            .id("NOT 1")
            .state(OrderState.PURCHASED)
            .build();

        when(orderRepository.existsById("1")).thenReturn(true);

        Assertions.assertThrows(IdMismatchException.class, () -> {
            orderServiceImp.replaceOrder("1",orderToReplace);
        });
    }
    @Test
    void whenReplacingOrderDTO_throwOnOrderNotFound() {
        OrderDTO orderToReplace = OrderDTO.builder()
            .id("1")
            .state(OrderState.PURCHASED)
            .build();

        when(orderRepository.existsById("1")).thenReturn(false);

        Assertions.assertThrows(EntityNotFoundException.class, () -> {
            orderServiceImp.replaceOrder("1",orderToReplace);
        });
    }

    @Test
    void whenReplacingOrderDTO_thenReplaceOrder() throws IllegalAccessException {
        OrderDTO orderToReplace = OrderDTO.builder()
            .id("1")
            .orderDate(LocalDateTime.now(fixedClock))
            .state(OrderState.PURCHASED)
            .build();

        Order expectedToReplace = Order.builder()
            .id("1")
            .orderDate(LocalDateTime.now(fixedClock))
            .state(OrderState.PURCHASED)
            .build();

        Order expectedReplaced = Order.builder()
            .id("1")
            .orderDate(LocalDateTime.now(fixedClock))
            .state(OrderState.PURCHASED)
            .build();


        when(orderRepository.existsById("1")).thenReturn(true);
        when(orderRepository.save(expectedToReplace)).thenReturn(expectedReplaced);

        OrderDTO replacedOrder = orderServiceImp.replaceOrder("1",orderToReplace);
    }





    public Order mapToEntity(OrderDTO orderDTO) {
        return modelMapper.map(orderDTO, Order.class);
    }

    public OrderDTO mapToDTO(Order order) {
        return modelMapper.map(order, OrderDTO.class);
    }
}
