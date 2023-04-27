package com.enterpriseintellijence.enterpriseintellijence.dto;

import com.enterpriseintellijence.enterpriseintellijence.dto.enums.OrderState;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString
public class OrderDTO {

    private String id;

    private OrderState state;

    private LocalDateTime orderDate;

    private ProductDTO product;
    private UserDTO user;
    private DeliveryDTO delivery;
    private OfferDTO offer;
}
