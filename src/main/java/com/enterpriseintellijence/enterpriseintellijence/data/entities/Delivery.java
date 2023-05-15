package com.enterpriseintellijence.enterpriseintellijence.data.entities;

import com.enterpriseintellijence.enterpriseintellijence.data.entities.embedded.Address;

import com.enterpriseintellijence.enterpriseintellijence.data.entities.embedded.CustomMoney;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.hibernate.annotations.GenericGenerator;
import org.joda.money.Money;

@Data
@NoArgsConstructor
@Entity
@Builder
@AllArgsConstructor
@Table(name = "delivery")
public class Delivery {
    @Id
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "uuid2")
    @Column(length = 36, nullable = false, updatable = false)
    private String id;

    @OneToOne(mappedBy = "delivery")
    private Order order;

    @Column(name = "delivery_cost")
    private CustomMoney deliveryCost;

    private String shipper;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "country", column = @Column(name = "sender_country")),
        @AttributeOverride(name = "city", column = @Column(name = "sender_city")),
        @AttributeOverride(name = "street", column = @Column(name = "sender_street")),
        @AttributeOverride(name = "postalCode", column = @Column(name = "sender_postal_code"))
    })
    private Address senderAddress;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "country", column = @Column(name = "receiver_country")),
        @AttributeOverride(name = "city", column = @Column(name = "receiver_city")),
        @AttributeOverride(name = "street", column = @Column(name = "receiver_street")),
        @AttributeOverride(name = "postalCode", column = @Column(name = "receiver_postal_code"))
    })
    private Address receiverAddress;
}
