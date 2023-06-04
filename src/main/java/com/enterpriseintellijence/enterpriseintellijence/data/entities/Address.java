package com.enterpriseintellijence.enterpriseintellijence.data.entities;

import com.enterpriseintellijence.enterpriseintellijence.data.entities.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name="delivery_address")
public class Address {
    @Id
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "uuid2")
    @Column(length = 36, nullable = false, updatable = false)
    private String id;

    private String header;
    private String country;
    private String city;
    private String street;

    @Column(name = "zip_code")
    private String zipCode;
    private String phoneNumber;

    @OneToOne(mappedBy = "defaultAddress",fetch = FetchType.LAZY)
    private User defaultUser;

    @ManyToOne
    @JoinColumn(name = "owner_address")
    private User user;

}
