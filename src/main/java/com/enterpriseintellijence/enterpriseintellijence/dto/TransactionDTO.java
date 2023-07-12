package com.enterpriseintellijence.enterpriseintellijence.dto;

import com.enterpriseintellijence.enterpriseintellijence.dto.enums.TransactionState;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class TransactionDTO {

    @NotNull
    private String id;

    @Past
    private LocalDateTime creationTime;

    @Min(0)
    @NotNull
    private CustomMoneyDTO amount;

    @NotNull
    private TransactionState transactionState;

    @NotNull
    private String paymentMethod;

    @NotNull
    private String paymentMethodOwner;

}
