package com.enterpriseintellijence.enterpriseintellijence.dto.creation;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.validator.constraints.Length;

@Data
@NoArgsConstructor
@ToString
@Builder
@AllArgsConstructor
public class AddressCreateDTO {

    @NotNull
    @Length(max = 100)
    private String header;

    @NotNull
    @Length(max = 100)
    private String country;

    @NotNull
    @Length(max = 100)
    private String city;

    @NotNull
    @Length(max = 100)
    private String street;

    @NotNull
    @Length(max = 10)
    private String zipCode;

    @NotNull
    @Length(min=6, max = 20)
    private String phoneNumber;

    @NotNull
    private Boolean isDefault;

}
