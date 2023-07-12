package com.enterpriseintellijence.enterpriseintellijence.data.entities;

import com.enterpriseintellijence.enterpriseintellijence.dto.enums.OrderState;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "uuid2")
    @Column(length = 36, nullable = false, updatable = false)
    private String id;

    @Column(name = "order_create_date",nullable = false)
    private LocalDateTime orderDate;

    @Column(name = "order_update_date",nullable = false)
    private LocalDateTime orderUpdateDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderState state;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "order_product", nullable = false)
    private Product product;

    @ManyToOne(cascade = {CascadeType.PERSIST})
    @JoinColumn(name = "order_user", nullable = false)
    private User user;

    @OneToOne(cascade = {CascadeType.PERSIST})
    @JoinColumn(name = "order_delivery")
    private Delivery delivery;

    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name="delivery_address",nullable = false)
    private Address deliveryAddress;

    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "order_offer")
    private Offer offer;

    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "order_transaction")
    private Transaction transaction;
}
