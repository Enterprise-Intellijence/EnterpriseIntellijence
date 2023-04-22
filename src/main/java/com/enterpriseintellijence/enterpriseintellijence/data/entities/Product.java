package com.enterpriseintellijence.enterpriseintellijence.data.entities;

import com.enterpriseintellijence.enterpriseintellijence.data.entities.embedded.Address;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.Availability;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.Visibility;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

    @Column(name = "title", length = 100)
    private String title;

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "price", nullable = false)
    private Float price;

    @Column(name = "brand")
    private String brand;

    private String condition;
    //TODO: che significa condition?
    // usato, nuovo, quasi nuovo, ecc

    private Address address;

    @JoinColumn(name = "delivery_type")
    private String deliveryType;

    private Integer views;

    @JoinColumn(name = "upload_date")
    private LocalDateTime uploadDate;


    @Enumerated(EnumType.STRING)
    private Visibility visibility;

    @Enumerated(EnumType.STRING)
    private Availability availability;


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
