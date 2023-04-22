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

    // TODO: usare pattern per la carta di credito

    private String creditCard;
    private String expiryDate;
    private String owner;
    private UserDTO defaultUser;
    private UserDTO ownerUser;

}
