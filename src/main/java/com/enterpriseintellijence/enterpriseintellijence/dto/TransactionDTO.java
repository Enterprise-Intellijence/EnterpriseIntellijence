package com.enterpriseintellijence.enterpriseintellijence.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.joda.money.Money;

@Data
@NoArgsConstructor
@ToString
public class TransactionDTO {

    private String id;
    @Min(0)
    @NotNull
    private Money amount;
    @NotNull
    private PaymentMethodDTO paymentMethod;

}
