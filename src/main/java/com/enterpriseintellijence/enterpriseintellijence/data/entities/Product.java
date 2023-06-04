package com.enterpriseintellijence.enterpriseintellijence.data.entities;

import com.enterpriseintellijence.enterpriseintellijence.data.entities.embedded.CustomMoney;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.*;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

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

    //@Column(name = "price", nullable = false)
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name="price",column=@Column(name="product_price")),
            @AttributeOverride(name="currency",column=@Column(name="product_currency"))
    })
    private CustomMoney productCost;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name="price",column=@Column(name="delivery_price")),
            @AttributeOverride(name="currency",column=@Column(name="delivery_currency"))
    })
    private CustomMoney deliveryCost;


    @Column(name = "brand")
    private String brand;

    @Column(name = "condition")
    @Enumerated(EnumType.STRING)
    private Condition condition;
    //TODO: che significa condition?
    // usato, nuovo, quasi nuovo, ecc

/*    @Embedded
    private Address address;*/

    @Enumerated(EnumType.STRING)
    @Column(name = "delivery_type")
    private ProductSize productSize;

    @Column(name = "views", nullable = false)
    private Integer views;

    @Column(name = "upload_date", nullable = false)
    private LocalDateTime uploadDate;

    @Column(name = "last_update_date", nullable = false)
    private LocalDateTime lastUpdateDate;


    @Enumerated(EnumType.STRING)
    @Column(name = "visibility", nullable = false)
    private Visibility visibility;

    @Enumerated(EnumType.STRING)
    @Column(name = "availability", nullable = false)
    private Availability availability;

    @Enumerated(EnumType.STRING)
    @Column(name="main_category"/*,nullable = false*/)
    private ProductCategory productCategory;

    @Enumerated(EnumType.STRING)
    @Column(name="product_category",nullable = false)
    private ProductCategoryParent productCategoryParent;

    @Enumerated(EnumType.STRING)
    @Column(name="product_category_child",nullable = false)
    private ProductCategoryChild productCategoryChild;


    @Column(name = "like_number", nullable = false)
    private Integer likesNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id"/*, nullable = false*/)
    private User seller;

    @ManyToMany(mappedBy = "likedProducts",fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    private List<User> usersThatLiked;

    @OneToMany(mappedBy = "product",fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    private List<Offer> offers;

    @OneToMany(mappedBy = "product",fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    private List<Message> messages;

    @OneToOne(mappedBy = "product",fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    private Order order;

/*    @OneToOne(cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    @JoinColumn(name = "default_image")
    private ProductImage defaultImage;*/

    @OneToMany(mappedBy = "product",cascade = CascadeType.ALL)
    private List<ProductImage> productImages;

    @OneToMany(mappedBy = "reportedProduct",fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    private List<Report> reports;



}
