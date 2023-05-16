package com.enterpriseintellijence.enterpriseintellijence.dto;

import com.enterpriseintellijence.enterpriseintellijence.dto.enums.OrderState;
import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString
@Builder
@AllArgsConstructor
public class OrderDTO {

    @NotNull
    private String id;

    @NotNull
    private OrderState state;

    @Past
    private LocalDateTime orderDate;

    private ProductBasicDTO product;
    private UserBasicDTO user;
    private DeliveryDTO delivery;

    private OfferDTO offer;
    private TransactionDTO transaction;
}


