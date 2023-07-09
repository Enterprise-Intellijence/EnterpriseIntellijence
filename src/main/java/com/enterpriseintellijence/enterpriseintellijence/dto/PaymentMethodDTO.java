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

    @NotNull
    private String id;

    @NotNull
    @Length(min = 19, max = 19)
    private String creditCard;

    @NotNull
    @JsonFormat(pattern="dd-MM-yyyy")
    private LocalDate expiryDate;

    @NotNull
    private Boolean isDefault;

    @NotNull
    @Length(max = 25)
    private String owner;

}
