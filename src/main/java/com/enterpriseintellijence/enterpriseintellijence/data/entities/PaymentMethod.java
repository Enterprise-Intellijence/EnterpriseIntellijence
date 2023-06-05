package com.enterpriseintellijence.enterpriseintellijence.data.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Table(name = "payment_methods")
@Entity
public class PaymentMethod {
    @Id
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "uuid2")
    @Column(length = 36, nullable = false, updatable = false)
    private String id;


    private String creditCard;

    private LocalDate expiryDate;  // DD//MM/YYYY
    private String owner;

/*    @ToString.Exclude
    @OneToOne(mappedBy = "defaultPaymentMethod",fetch = FetchType.LAZY)
    private User defaultUser;*/

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User ownerUser;

    @OneToMany(mappedBy = "paymentMethod")
    private List<Transaction> transaction;




}
