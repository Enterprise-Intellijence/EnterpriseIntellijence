package com.enterpriseintellijence.enterpriseintellijence.data.services;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class DeliveryServiceImpTest {

    /*private ModelMapper modelMapper;
    private DeliveryService deliveryService;

    @Mock
    private DeliveryRepository deliveryRepository;

    Clock fixedClock = Clock.fixed(Instant.parse("2022-12-21T10:15:30.00Z"), ZoneId.of("UTC"));

    private DeliveryDTO defaultDeliveryDTO;
    private Delivery defaultDeliveryEntity;
    private Order defaultOrderEntity;
    private OrderDTO defaultOrderDTO;
    private UserDTO defaultUserDTO;
    private User defaultUserEntity;
    private Address defaultAddressEntity;
    private AddressDTO defaultAddressDTO;
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
    }*/
}
