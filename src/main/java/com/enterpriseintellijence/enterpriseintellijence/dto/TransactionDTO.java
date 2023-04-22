package com.enterpriseintellijence.enterpriseintellijence.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString
public class TransactionDTO {

    private String id;

    // TODO: usare classe apposita per i soldi
    private Float amount;
    private PaymentMethodDTO paymentMethod;

}
