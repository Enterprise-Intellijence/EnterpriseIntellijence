package com.enterpriseintellijence.enterpriseintellijence.dto.basics;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.validator.constraints.Length;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@ToString
public class PaymentMethodBasicDTO {

    @NotNull
    private String id;

    @NotNull
    @Length(min = 19, max = 19)
    private String creditCard;

    @NotNull
    private boolean isDefault;


}
