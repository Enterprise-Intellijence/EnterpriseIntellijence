package com.enterpriseintellijence.enterpriseintellijence.dto.creation;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class PaymentMethodCreateDTO {

    @NotBlank
    @Length(min = 19, max = 19)
    private String creditCard;

    @NotBlank
    @JsonFormat(pattern="dd-MM-yyyy") //DATE MM/AAAA
    private LocalDate expiryDate;

    @NotNull
    private boolean isDefault;

    @NotBlank
    @Length(max = 25)
    private String owner;

}
