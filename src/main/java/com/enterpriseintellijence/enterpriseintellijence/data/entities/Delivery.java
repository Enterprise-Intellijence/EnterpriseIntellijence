package com.enterpriseintellijence.enterpriseintellijence.data.entities;

import com.enterpriseintellijence.enterpriseintellijence.data.entities.embedded.CustomMoney;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.DeliveryStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Builder
@AllArgsConstructor
@Table(name = "delivery")
public class  Delivery {
    @Id
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "uuid2")
    @Column(length = 36, nullable = false, updatable = false)
    private String id;

    @OneToOne(mappedBy = "delivery",cascade = {CascadeType.PERSIST})
    private Order order;

    private LocalDateTime sendTime;

    private LocalDateTime deliveredTime;

    @Column(name = "delivery_cost")
    private CustomMoney deliveryCost;

    private String shipper;

    @Enumerated(EnumType.STRING)
    private DeliveryStatus deliveryStatus;

    @ManyToOne
    @JoinColumn(name = "sender_address")
    private Address senderAddress;


    @ManyToOne
    @JoinColumn(name = "receiver_address")
    private Address receiverAddress;
}
