package com.enterpriseintellijence.enterpriseintellijence.dto;

import com.enterpriseintellijence.enterpriseintellijence.dto.basics.UserBasicDTO;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;

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

    @NotBlank  //DATE MM/AAAA
    private LocalDate expiryDate;

    @NotBlank
    @Length(max = 25)
    private String owner;

    private UserBasicDTO ownerUser;

}
