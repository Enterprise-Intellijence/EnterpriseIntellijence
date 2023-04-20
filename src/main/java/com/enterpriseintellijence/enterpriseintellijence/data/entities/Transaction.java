package com.enterpriseintellijence.enterpriseintellijence.data.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

@Data
@NoArgsConstructor
@Table(name = "transaction")
@Entity
public class Transaction {
    @Id
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "uuid2")
    @Column(length = 36, nullable = false, updatable = false)
    private String id;

    private Float amount;
    //TODO: serve paymentMethod qui?
    private String paymentMethod;
}
