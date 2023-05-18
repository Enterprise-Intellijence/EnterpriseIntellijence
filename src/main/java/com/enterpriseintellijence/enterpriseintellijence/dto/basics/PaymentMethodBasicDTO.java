package com.enterpriseintellijence.enterpriseintellijence.dto.basics;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.validator.constraints.Length;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@ToString
public class PaymentMethodBasicDTO {
    private String id;

    @NotBlank
    @Length(min = 19, max = 19)
    private String creditCard;

}
