package com.enterpriseintellijence.enterpriseintellijence.dto;

import com.enterpriseintellijence.enterpriseintellijence.dto.basics.UserBasicDTO;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

    @Length(min = 19, max = 19)
    private String creditCard;

    @JsonFormat(pattern="dd-MM-yyyy") //DATE MM/AAAA
    private LocalDate expiryDate;

    @NotNull
    private boolean isDefault;

    @Length(max = 25)
    private String owner;

    //private UserBasicDTO ownerUser;

}
