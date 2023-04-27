package com.enterpriseintellijence.enterpriseintellijence.data.services;

import com.enterpriseintellijence.enterpriseintellijence.data.entities.*;
import com.enterpriseintellijence.enterpriseintellijence.data.entities.Order;
import com.enterpriseintellijence.enterpriseintellijence.data.repository.OrderRepository;
import com.enterpriseintellijence.enterpriseintellijence.dto.*;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.OrderState;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.event.annotation.BeforeTestClass;
import org.springframework.test.context.event.annotation.BeforeTestExecution;
import org.springframework.test.context.event.annotation.BeforeTestMethod;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class OrderServiceImpTest {

    @InjectMocks
    private OrderServiceImp orderServiceImp;
    @Mock
    private OrderRepository orderRepository;

    public ModelMapper modelMapper;

    @BeforeEach
    public void setUp() {
        modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setFieldMatchingEnabled(true)
                .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE)
                .setMatchingStrategy(MatchingStrategies.STRICT)
                .setAmbiguityIgnored(true);
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
    void shouldSaveOneOrder() {


        Order orderToSave = Order.builder()
                .orderDate(LocalDateTime.now())
                .state(OrderState.PURCHASED)
                .product(mock(Product.class))
                .delivery(mock(Delivery.class))
                .offer(mock(Offer.class))
                .user(mock(User.class))
                .build();


        when(orderRepository.save(any(Order.class))).thenReturn(orderToSave);
        OrderDTO saveOrder = orderServiceImp.createOrder(mapToDTO(orderToSave));  // da errore qui
        System.out.println("ciao");
        assertThat(saveOrder).usingRecursiveComparison().isEqualTo(orderToSave); // sbagliato: si fa equal tra Order e OrderDTO
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
