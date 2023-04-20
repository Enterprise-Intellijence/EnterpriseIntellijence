package com.enterpriseintellijence.enterpriseintellijence.data.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.util.List;

@Data
@NoArgsConstructor
@Table(name = "payment_methods")
@Entity
public class PaymentMethod {
    @Id
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "uuid2")
    @Column(length = 36, nullable = false, updatable = false)
    private String id;

    private String creditCard;
    private String expiryDate;
    private String owner;
    @OneToOne(mappedBy = "defaultPaymentMethod")
    private User defaultUser;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User ownerUser;



}
