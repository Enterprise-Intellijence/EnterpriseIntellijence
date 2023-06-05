package com.enterpriseintellijence.enterpriseintellijence.data.entities;

import com.enterpriseintellijence.enterpriseintellijence.data.entities.User;
import jakarta.persistence.*;
import lombok.*;
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

    @Column(nullable = false)
    private String header;

    @Column(nullable = false)
    private String country;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String street;

    @Column(name = "zip_code")
    private String zipCode;

    @Column(nullable = false)
    private String phoneNumber;

/*    @ToString.Exclude
    @OneToOne(mappedBy = "defaultAddress",fetch = FetchType.LAZY,cascade = CascadeType.PERSIST)
    private User defaultUser;*/

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_address")
    private User user;

}
