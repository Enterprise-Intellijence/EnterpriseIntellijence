package com.enterpriseintellijence.enterpriseintellijence.data.entities;


import com.enterpriseintellijence.enterpriseintellijence.data.entities.embedded.Address;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.UserRole;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "uuid2")
    @Column(length = 36, nullable = false, updatable = false)
    private String id;

    @Column(unique = true, nullable = false)
    private String username;

    private String password;

    @Column(unique = true, nullable = false, length = 100)
    private String email;
    private byte[] photo;

    private Address address;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    @OneToOne
    @JoinColumn(name = "default_payment_method")
    private PaymentMethod defaultPaymentMethod;

    @OneToMany(mappedBy = "ownerUser")
    private List<PaymentMethod> paymentMethods;

    @OneToMany(mappedBy = "offerer")
    private List<Offer> offersMade;

    @ManyToMany(mappedBy = "follows")
    private List<User> followers;

    @OneToMany(mappedBy = "seller")
    private List<Product> soldProducts;

    @ManyToMany
    private List<User> follows;

    @ManyToMany
    @JoinTable(name = "user_likes", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "product_id"))
    private List<Product> likedProducts;

    @OneToMany(mappedBy = "sendUser")
    private List<Message> sentMessages;

    @OneToMany(mappedBy = "receivedUser")
    private List<Message> receivedMessages;

    @OneToMany(mappedBy = "user")
    private List<Order> orders;

    @OneToMany(mappedBy = "reviewed")
    private List<Review> receivedReviews;

    @OneToMany(mappedBy = "reviewer")
    private List<Review> sentReviews;

}
