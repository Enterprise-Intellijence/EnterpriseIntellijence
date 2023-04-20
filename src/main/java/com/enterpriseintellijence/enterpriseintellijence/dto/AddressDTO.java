package com.enterpriseintellijence.enterpriseintellijence.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString
public class AddressDTO {

    private String country;
    private String city;
    private String street;
    private String postalCode;

}
