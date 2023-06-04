package com.enterpriseintellijence.enterpriseintellijence.data.entities;


import com.enterpriseintellijence.enterpriseintellijence.dto.enums.Provider;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.UserRole;

import com.enterpriseintellijence.enterpriseintellijence.dto.enums.UserStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "users")
@Builder
@AllArgsConstructor
public class User implements UserDetails {
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

    @Column(name = "bio", length = 500)
    private String bio;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "photo_profile")
    private UserImage photoProfile;

    @Enumerated(EnumType.STRING)
    private Provider provider;

    @OneToOne(cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    @JoinColumn(name = "default_address_delivery")
    private Address defaultAddress;

    @OneToMany(mappedBy = "user",fetch = FetchType.LAZY)
    private List<Address> addresses;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Column(name = "email_verified", nullable = false)
    private boolean emailVerified;

    @OneToOne(cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    @JoinColumn(name = "default_payment_method")
    private PaymentMethod defaultPaymentMethod;

    @OneToMany(mappedBy = "ownerUser",fetch = FetchType.LAZY)
    private List<PaymentMethod> paymentMethods;

    @OneToMany(mappedBy = "offerer",fetch = FetchType.LAZY)
    private List<Offer> offersMade;

    @OneToMany(mappedBy = "seller",fetch = FetchType.LAZY)
    private List<Product> sellingProducts;

    @ManyToMany()
    @JoinTable(name = "user_followers", joinColumns = @JoinColumn(name = "followers_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
    private List<User> followers;

    @ManyToMany()
    @JoinTable(name = "user_following", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "following_id"))
    private List<User> following;

    @Column(name = "followers_number", nullable = false)
    private int followers_number;

    @Column(name = "following_number", nullable = false)
    private int following_number;

    @Column(name = "reviews_total_sum", nullable = false)
    private int reviews_total_sum;

    @Column(name = "reviews_number", nullable = false)
    private int reviews_number;


    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_likes", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "product_id"))
    private List<Product> likedProducts;

    @OneToMany(mappedBy = "sendUser",fetch = FetchType.LAZY)
    private List<Message> sentMessages;

    @OneToMany(mappedBy = "receivedUser",fetch = FetchType.LAZY)
    private List<Message> receivedMessages;

    @OneToMany(mappedBy = "user",fetch = FetchType.LAZY)
    private List<Order> orders;

    @OneToMany(mappedBy = "reviewed",fetch = FetchType.LAZY)
    private List<Review> receivedReviews;

    @OneToMany(mappedBy = "reviewer",fetch = FetchType.LAZY)
    private List<Review> sentReviews;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private UserStatus status;

    @OneToMany(mappedBy = "reporterUser",fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Report> reports;

    @OneToMany(mappedBy = "reportedUser",fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Report> reported;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        switch (role){
            case USER -> {
                return Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));
            }
            case ADMIN -> {
                return Collections.singleton(new SimpleGrantedAuthority("ROLE_ADMIN"));
            }
            case SUPER_ADMIN -> {
                return Collections.singleton(new SimpleGrantedAuthority("ROLE_SUPER_ADMIN"));
            }
        }
        return null;
    }

    public void addReview(int vote) {
        reviews_number++;
        reviews_total_sum += vote;
    }

    public void removeReview(int vote) {
        reviews_number--;
        reviews_total_sum -= vote;
    }

    public void editReview(int oldVote, int newVote) {
        reviews_total_sum -= oldVote;
        reviews_total_sum += newVote;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !status.equals(UserStatus.BANNED);
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return emailVerified;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return  password;
    }

}
