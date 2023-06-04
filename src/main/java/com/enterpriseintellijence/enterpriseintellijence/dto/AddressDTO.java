package com.enterpriseintellijence.enterpriseintellijence.dto;

import com.enterpriseintellijence.enterpriseintellijence.dto.basics.UserBasicDTO;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class AddressDTO {
    private String id;
    private String header;
    private String country;
    private String city;
    private String street;
    private String zipCode;
    private String phoneNumber;
    private UserBasicDTO user;

}
