package com.enterpriseintellijence.enterpriseintellijence.data.entities;


import com.enterpriseintellijence.enterpriseintellijence.data.entities.embedded.Address;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.Provider;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.UserRole;

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
    private byte[] photo;

    @Enumerated(EnumType.STRING)
    private Provider provider;

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

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        switch (role){
            case USER -> {
                return Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));
            }
            case ADMIN -> {
                return Collections.singleton(new SimpleGrantedAuthority("ROLE_ADMIN"));
            }
        }
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
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
