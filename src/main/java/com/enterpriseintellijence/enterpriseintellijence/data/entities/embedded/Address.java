package com.enterpriseintellijence.enterpriseintellijence.data.entities.embedded;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Address {

    private String country;
    private String city;

    private String street;

    @Column(name = "postal_code")
    private String postalCode;

}
