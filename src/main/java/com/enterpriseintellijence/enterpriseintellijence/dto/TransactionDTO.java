package com.enterpriseintellijence.enterpriseintellijence.dto;

import com.enterpriseintellijence.enterpriseintellijence.data.entities.embedded.CustomMoney;
import com.enterpriseintellijence.enterpriseintellijence.dto.basics.OrderBasicDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.basics.PaymentMethodBasicDTO;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.joda.money.Money;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class TransactionDTO {

    private String id;
    private LocalDateTime creationTime;

    @Min(0)
    @NotNull
    private CustomMoneyDTO amount;
    @NotNull
    private PaymentMethodBasicDTO paymentMethod;

    private OrderBasicDTO order;

}
