package com.enterpriseintellijence.enterpriseintellijence.data.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

@Data
@NoArgsConstructor
@Entity
@Table(name = "offers")
public class Offer {

    @Id
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "uuid2")
    @Column(length = 36, nullable = false, updatable = false)
    private String id;

    private Float amount;

    private String state;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User offerer;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @OneToOne
    @JoinColumn(name = "message_id")
    private Message message;

    @OneToOne(mappedBy = "offer")
    private Order order;
}
