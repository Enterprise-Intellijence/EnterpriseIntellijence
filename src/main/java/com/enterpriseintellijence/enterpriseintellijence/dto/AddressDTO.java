package com.enterpriseintellijence.enterpriseintellijence.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class AddressDTO {

    private String country;
    private String city;
    private String street;
    private String postalCode;

}
