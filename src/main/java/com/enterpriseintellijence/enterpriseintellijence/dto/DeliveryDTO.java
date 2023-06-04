package com.enterpriseintellijence.enterpriseintellijence.dto;

import com.enterpriseintellijence.enterpriseintellijence.dto.basics.OrderBasicDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.DeliveryStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class DeliveryDTO {
    @NotNull
    private String Id;

    private OrderBasicDTO order;

    private LocalDateTime sendTime;

    private LocalDateTime deliveredTime;

    private CustomMoneyDTO deliveryCost;

    @Length(max = 50)
    private String shipper;

    private DeliveryStatus deliveryStatus;

    private AddressDTO senderAddress;

    private AddressDTO receiverAddress;
}
