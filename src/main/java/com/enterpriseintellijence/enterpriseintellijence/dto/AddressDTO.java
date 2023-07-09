package com.enterpriseintellijence.enterpriseintellijence.dto;

import com.enterpriseintellijence.enterpriseintellijence.dto.basics.UserBasicDTO;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.validator.constraints.Length;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class AddressDTO {
    @NotNull
    private String id;

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
    @Length(min=6, max=20)
    private String phoneNumber;

    @NotNull
    private boolean isDefault;


}
