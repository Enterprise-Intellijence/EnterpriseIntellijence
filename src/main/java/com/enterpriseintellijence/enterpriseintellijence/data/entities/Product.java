package com.enterpriseintellijence.enterpriseintellijence.data.entities;

import com.enterpriseintellijence.enterpriseintellijence.data.entities.embedded.Address;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.*;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @see com.enterpriseintellijence.enterpriseintellijence.dto.ProductDTO
 */
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

    @Column(name = "condition")
    @Enumerated(EnumType.STRING)
    private Condition condition;
    //TODO: che significa condition?
    // usato, nuovo, quasi nuovo, ecc

    @Embedded
    private Address address;

    @Column(name = "delivery_type")
    private ProductSize productSize;

    @Column(name = "views", nullable = false)
    private Integer views;

    @JoinColumn(name = "upload_date", nullable = false)
    private LocalDateTime uploadDate;


    @Enumerated(EnumType.STRING)
    @Column(name = "visibility", nullable = false)
    private Visibility visibility;

    @Enumerated(EnumType.STRING)
    @Column(name = "availability", nullable = false)
    private Availability availability;

    @Enumerated(EnumType.STRING)
    @Column(name="product_category",nullable = false)
    private ProductCategory productCategory;

    @ManyToOne
    @JoinColumn(name = "user_id"/*, nullable = false*/)
    private User seller;

    @ManyToMany(mappedBy = "likedProducts")
    private List<User> usersThatLiked;

    @OneToMany(mappedBy = "product")
    private List<Offer> offers;

    @OneToMany(mappedBy = "product")
    private List<Message> messages;

    @OneToOne(mappedBy = "product")
    private Order order;



}
