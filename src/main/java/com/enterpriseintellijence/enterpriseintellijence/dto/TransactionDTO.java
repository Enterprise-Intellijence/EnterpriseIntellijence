package com.enterpriseintellijence.enterpriseintellijence.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString
public class TransactionDTO {

    private String id;
    private Float amount;
    private String paymentMethod;

}
