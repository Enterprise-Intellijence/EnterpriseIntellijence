package com.enterpriseintellijence.enterpriseintellijence.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.validator.constraints.Length;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@ToString
public class PaymentMethodDTO {

    private String id;

    @NotBlank
    @Length(min = 12, max = 12)
    private String creditCard;

    @NotBlank
    @Length(min = 5, max = 5)
    private String expiryDate;   //WHY not DATE?

    @NotBlank
    @Length(max = 25)
    private String owner;

    private UserFullDTO defaultUser;

    private UserFullDTO ownerUser;

}
