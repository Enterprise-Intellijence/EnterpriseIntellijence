package com.enterpriseintellijence.enterpriseintellijence.dto;

import com.enterpriseintellijence.enterpriseintellijence.data.entities.embedded.Address;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import org.joda.money.Money;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class DeliveryDTO {

    private String Id;

    private OrderDTO order;

    @PositiveOrZero
    private Money deliveryCost;

    @Length(max = 50)
    private String shipper;

    private Address senderAddress;

    private Address receiverAddress;
}
