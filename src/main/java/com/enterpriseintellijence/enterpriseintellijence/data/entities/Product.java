package com.enterpriseintellijence.enterpriseintellijence.data.entities;

import com.enterpriseintellijence.enterpriseintellijence.data.entities.embedded.CustomMoney;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.*;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.ArrayList;
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

    @Column(name = "title", length = 100, nullable = false)
    private String title;

    @Column(name = "description", length = 1000)
    private String description;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name="price",column=@Column(name="product_price",nullable = false)),
            @AttributeOverride(name="currency",column=@Column(name="product_currency",nullable = false))
    })
    private CustomMoney productCost;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name="price",column=@Column(name="delivery_price",nullable = false)),
            @AttributeOverride(name="currency",column=@Column(name="delivery_currency",nullable = false))
    })
    private CustomMoney deliveryCost;

    @Column(name = "brand")
    private String brand;

    @Column(name = "condition",nullable = false)
    @Enumerated(EnumType.STRING)
    private Condition condition;


    //si riferisce alle dimensioni dell'imballo della spedizione
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private ProductCategory productCategory;

    @Column(name = "like_number", nullable = false)
    private Integer likesNumber ;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User seller;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JoinTable(
            name = "user_likes",
            joinColumns = @JoinColumn(name = "product_id",nullable = false),
            inverseJoinColumns = @JoinColumn(name = "user_id",nullable = false))
    List<User> usersThatLiked = new ArrayList<>();

    @OneToMany(mappedBy = "product",fetch = FetchType.LAZY)
    private List<Offer> offers =new ArrayList<>();

    @OneToMany(mappedBy = "product",fetch = FetchType.LAZY)
    private List<Message> messages = new ArrayList<>();

    @OneToOne(mappedBy = "product",fetch = FetchType.LAZY)
    private Order order;

    @OneToMany(mappedBy = "product",fetch = FetchType.LAZY,cascade = CascadeType.REMOVE)
    private List<ProductImage> productImages;

    @OneToMany(mappedBy = "reportedProduct",fetch = FetchType.LAZY)
    private List<Report> reports;

    @PreRemove
    private void preRemove(){
        this.usersThatLiked.clear();
        this.offers.clear();
        this.messages.clear();
        this.order = null;
        // TODO: 26/06/2023 rimuovere le immagini prima della consegna
        //this.productImages.clear();
        for(Report report:this.reports){
            report.setReportedProduct(null);
        }
    }

}
