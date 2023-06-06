package com.enterpriseintellijence.enterpriseintellijence.dto.creation;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@ToString
@Builder
@AllArgsConstructor
public class AddressCreateDTO {
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
