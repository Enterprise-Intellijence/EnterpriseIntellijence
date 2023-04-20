package com.enterpriseintellijence.enterpriseintellijence.data.entities;

import com.enterpriseintellijence.enterpriseintellijence.data.entities.embedded.Address;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "products")
@Inheritance(strategy = InheritanceType.JOINED)
public class Product {
    @Id
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "uuid2")
    @Column(length = 36, nullable = false, updatable = false)
    private String id;

    private String description;

    private Float price;

    private String brand;

    private String condition; //TODO: che significa condition?

    private Address address;

    @JoinColumn(name = "delivery_type")
    private String deliveryType;

    private Integer views;

    @JoinColumn(name = "upload_date")
    private LocalDate uploadDate;

    private String visibility;

    private Boolean availability;

    @Column(name = "send_date")
    private LocalDate sendDate;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User seller;

    @ManyToMany(mappedBy = "likes")
    private List<User> users;

    @OneToMany(mappedBy = "product")
    private List<Offer> offers;

    @OneToMany(mappedBy = "product")
    private List<Message> messages;

    @OneToOne(mappedBy = "product")
    private Order order;



}
