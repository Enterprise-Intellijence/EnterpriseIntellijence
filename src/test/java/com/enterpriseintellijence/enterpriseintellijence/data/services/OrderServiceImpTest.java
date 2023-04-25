package com.enterpriseintellijence.enterpriseintellijence.data.services;

import com.enterpriseintellijence.enterpriseintellijence.data.entities.*;
import com.enterpriseintellijence.enterpriseintellijence.data.entities.Order;
import com.enterpriseintellijence.enterpriseintellijence.data.repository.OrderRepository;
import com.enterpriseintellijence.enterpriseintellijence.dto.*;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.OrderState;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class OrderServiceImpTest {

    @InjectMocks
    private OrderServiceImp orderServiceImp;
    @Mock
    private OrderRepository orderRepository;

    @MockBean
    public ModelMapper modelMapper;

    @BeforeEach
    public void setUp() {
        modelMapper = new ModelMapper();
    }

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

        Order order = mapToEntity(orderDTO);
        Order expectedOrder = Order.builder()
                .orderDate(now)
                .state(OrderState.PURCHASED)
                .product(mock(Product.class))
                .delivery(mock(Delivery.class))
                .offer(mock(Offer.class))
                .user(mock(User.class))
                .build();

        assertThat(expectedOrder).usingRecursiveComparison().isEqualTo(order);
    }
    @Test
    void shouldSaveOneOrder() {

        final var orderToSave = Order.builder()
                .orderDate(LocalDateTime.now())
                .state(OrderState.PURCHASED)
                .product(mock(Product.class))
                .delivery(mock(Delivery.class))
                .offer(mock(Offer.class))
                .user(mock(User.class))
                .build();

        when(orderRepository.save(any(Order.class))).thenReturn(orderToSave);
        final var saveOrder = orderServiceImp.createOrder(mapToDTO(orderToSave));


        assertThat(saveOrder).usingRecursiveComparison().isEqualTo(orderToSave);
        verify(orderRepository, times(1)).save(orderToSave);
        verifyNoMoreInteractions(orderRepository);
    }

    public Order mapToEntity(OrderDTO orderDTO) {
        return modelMapper.map(orderDTO, Order.class);
    }

    public OrderDTO mapToDTO(Order order) {
        return modelMapper.map(order, OrderDTO.class);
    }

}
