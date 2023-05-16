package com.enterpriseintellijence.enterpriseintellijence.dto;

import com.enterpriseintellijence.enterpriseintellijence.data.entities.embedded.CustomMoney;
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
    private CustomMoney amount;
    @NotNull
    private String paymentMethod;

}
