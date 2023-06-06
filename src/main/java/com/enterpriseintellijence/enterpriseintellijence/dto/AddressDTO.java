package com.enterpriseintellijence.enterpriseintellijence.dto;

import com.enterpriseintellijence.enterpriseintellijence.dto.basics.UserBasicDTO;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class AddressDTO {
    @NotNull
    private String id;

    @NotNull
    private String header;

    @NotNull
    private String country;

    @NotNull
    private String city;

    @NotNull
    private String street;

    @NotNull
    private String zipCode;

    @NotNull
    private String phoneNumber;

    @NotNull
    private boolean isDefault;


}
