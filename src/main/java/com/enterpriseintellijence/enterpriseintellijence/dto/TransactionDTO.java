package com.enterpriseintellijence.enterpriseintellijence.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.joda.money.Money;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class TransactionDTO {

    private String id;
    @Min(0)
    @NotNull
    private Money amount;
    @NotNull
    private String paymentMethod;

}
