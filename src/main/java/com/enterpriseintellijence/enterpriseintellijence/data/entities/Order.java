package com.enterpriseintellijence.enterpriseintellijence.data.entities;

import com.enterpriseintellijence.enterpriseintellijence.dto.AddressDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.OrderState;

import jakarta.persistence.*;
import jakarta.servlet.annotation.HandlesTypes;
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

    @Column(name = "order_create_date")
    private LocalDateTime orderDate;

    @Column(name = "order_update_date")
    private LocalDateTime orderUpdateDate;

    @Enumerated(EnumType.STRING)
    private OrderState state;

    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "order_product")
    private Product product;

    @ManyToOne(cascade = {CascadeType.PERSIST})
    @JoinColumn(name = "order_user")
    private User user;

    @OneToOne(cascade = {CascadeType.PERSIST})
    @JoinColumn(name = "order_delivery")
    private Delivery delivery;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="delivery_address")
    private Address deliveryAddress;

    @OneToOne()
    @JoinColumn(name = "order_offer"/*,nullable = false*/)
    private Offer offer;

    @OneToOne(cascade = {CascadeType.REMOVE})
    @JoinColumn(name = "order_transaction")
    private Transaction transaction;
}
