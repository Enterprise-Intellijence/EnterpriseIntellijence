package com.enterpriseintellijence.enterpriseintellijence.data.entities;

import jakarta.persistence.*;
import jakarta.servlet.annotation.HandlesTypes;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

@Data
@NoArgsConstructor
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "uuid2")
    @Column(length = 36, nullable = false, updatable = false)
    private String id;

    private String state;

    @OneToOne
    @JoinColumn(name = "order_product")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "order_user")
    private User user;

    @OneToOne
    @JoinColumn(name = "order_delivery")
    private Delivery delivery;

    @OneToOne
    @JoinColumn(name = "order_offer",nullable = false)
    private Offer offer;

}
