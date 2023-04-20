package com.enterpriseintellijence.enterpriseintellijence.dto;

import com.enterpriseintellijence.enterpriseintellijence.data.entities.User;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString
public class PaymentMethodDTO {

    private String id;
    private String creditCard;
    private String expiryDate;
    private String owner;
    private User defaultUser;
    private User ownerUser;

}
