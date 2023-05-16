package com.enterpriseintellijence.enterpriseintellijence.data.services;

import com.enterpriseintellijence.enterpriseintellijence.data.entities.Delivery;
import com.enterpriseintellijence.enterpriseintellijence.data.entities.Order;
import com.enterpriseintellijence.enterpriseintellijence.data.entities.User;
import com.enterpriseintellijence.enterpriseintellijence.data.entities.embedded.Address;
import com.enterpriseintellijence.enterpriseintellijence.data.repository.DeliveryRepository;
import com.enterpriseintellijence.enterpriseintellijence.dto.AddressDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.DeliveryDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.OrderDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.UserFullDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.OrderState;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.Provider;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.UserRole;
import org.joda.money.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class DeliveryServiceImpTest {

    private ModelMapper modelMapper;
    private DeliveryService deliveryService;

    @Mock
    private DeliveryRepository deliveryRepository;

    Clock fixedClock = Clock.fixed(Instant.parse("2022-12-21T10:15:30.00Z"), ZoneId.of("UTC"));

    private DeliveryDTO defaultDeliveryDTO;
    private Delivery defaultDeliveryEntity;
    private Order defaultOrderEntity;
    private OrderDTO defaultOrderDTO;
    private UserFullDTO defaultUserFullDTO;
    private User defaultUserEntity;
    private Address defaultAddressEntity;
    private AddressDTO defaultAddressDTO;
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

        defaultOrderEntity = Order.builder()
                .id("1")
                .orderDate(LocalDateTime.now(fixedClock))
                .state(OrderState.PURCHASED)
                .user(defaultUserEntity)
                .build();

        defaultOrderDTO = mapToDTO(defaultOrderEntity);

        defaultAddressEntity = Address.builder()
                .city("city")
                .country("country")
                .street("street")
                .postalCode("555")
                .build();

        defaultAddressDTO = modelMapper.map(defaultAddressEntity, AddressDTO.class);

        defaultDeliveryEntity = Delivery.builder()
                .id("1")
                .order(defaultOrderEntity)
                .deliveryCost(Money.parse("USD 10.00"))
                .shipper("shipper")
                .senderAddress(defaultAddressEntity)
                .receiverAddress(defaultAddressEntity)
                .build();

        defaultDeliveryDTO = mapToDTO(defaultDeliveryEntity);

        deliveryService = new DeliveryServiceImp(deliveryRepository, modelMapper);
    }

    @Test
    void whenMappingDeliveryEntityToDeliveryDto_thenCorrect() {
        DeliveryDTO deliveryDTO = DeliveryDTO.builder()
                .Id("1")
                .order(defaultOrderDTO)
                .deliveryCost(Money.parse("USD 10.00"))
                .shipper("shipper")
                .senderAddress(defaultAddressEntity)
                .receiverAddress(defaultAddressEntity)
                .build();

        Delivery delivery = mapToEntity(deliveryDTO);

        Delivery expectedDelivery = Delivery.builder()
                .id("1")
                .order(defaultOrderEntity)
                .deliveryCost(Money.parse("USD 10.00"))
                .shipper("shipper")
                .senderAddress(defaultAddressEntity)
                .receiverAddress(defaultAddressEntity)
                .build();

        assertThat(delivery).usingRecursiveComparison().isEqualTo(expectedDelivery);
    }

    public Order mapToEntity(OrderDTO orderDTO) {
        return modelMapper.map(orderDTO, Order.class);
    }

    public OrderDTO mapToDTO(Order order) {
        return modelMapper.map(order, OrderDTO.class);
    }

    public Delivery mapToEntity(DeliveryDTO delivery) {
        return modelMapper.map(delivery, Delivery.class);
    }

    public DeliveryDTO mapToDTO(Delivery deliveryDTO) {
        return modelMapper.map(deliveryDTO, DeliveryDTO.class);
    }
}
