package com.enterpriseintellijence.enterpriseintellijence.dto;

import com.enterpriseintellijence.enterpriseintellijence.data.entities.embedded.Address;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;
import org.joda.money.Money;

@Data
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
