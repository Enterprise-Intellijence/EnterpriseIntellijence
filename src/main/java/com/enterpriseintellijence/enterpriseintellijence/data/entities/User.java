package com.enterpriseintellijence.enterpriseintellijence.data.entities;


import com.enterpriseintellijence.enterpriseintellijence.dto.enums.Provider;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.UserRole;

import com.enterpriseintellijence.enterpriseintellijence.dto.enums.UserStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

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

    @OneToOne(mappedBy = "user",  fetch = FetchType.LAZY)
    private UserImage photoProfile;

    @Enumerated(EnumType.STRING)
    private Provider provider;



    @OneToMany(mappedBy = "user",fetch = FetchType.LAZY)
    private List<Address> addresses = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Column(name = "email_verified", nullable = false)
    private boolean emailVerified;

/*    @OneToOne(cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    @JoinColumn(name = "default_payment_method")
    private PaymentMethod defaultPaymentMethod;*/

    @OneToMany(mappedBy = "ownerUser",fetch = FetchType.LAZY)
    private List<PaymentMethod> paymentMethods;

    @OneToMany(mappedBy = "offerer",fetch = FetchType.LAZY)
    private List<Offer> offersMade;

    @OneToMany(mappedBy = "seller",fetch = FetchType.LAZY)
    private List<Product> sellingProducts = new ArrayList<>();

/*    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_followers", joinColumns = @JoinColumn(name = "followers_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
    private List<User> followers= new ArrayList<>();;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_following", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "following_id"))
    private List<User> following= new ArrayList<>();;*/

    @OneToMany(mappedBy="following",fetch = FetchType.LAZY)
    private List<Following> followers;

    @OneToMany(mappedBy="follower",fetch = FetchType.LAZY)
    private List<Following> following;

    @Column(name = "followers_number", nullable = false)
    private int followersNumber;

    @Column(name = "following_number", nullable = false)
    private int followingNumber;

    @Column(name = "reviews_total_sum", nullable = false)
    private int reviewsTotalSum;

    @Column(name = "reviews_number", nullable = false)
    private int reviewsNumber;

    @ManyToMany(mappedBy = "usersThatLiked",fetch = FetchType.LAZY)
/*    @JoinTable(
            name = "user_likes",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id"))*/
    List<Product> likedProducts= new ArrayList<>();

    @OneToMany(mappedBy = "sendUser",fetch = FetchType.LAZY)
    private List<Message> sentMessages;

    @OneToMany(mappedBy = "receivedUser",fetch = FetchType.LAZY)
    private List<Message> receivedMessages;

    @OneToMany(mappedBy = "user",fetch = FetchType.LAZY)
    private List<Order> orders;

    // TODO: 08/06/2023 problemi di conversione e caricamento
    @OneToMany(mappedBy = "reviewed",fetch = FetchType.LAZY)
    private List<Review> receivedReviews = new ArrayList<>();

    @OneToMany(mappedBy = "reviewer",fetch = FetchType.LAZY)
    private List<Review> sentReviews = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private UserStatus status;

    @OneToMany(mappedBy = "reporterUser",fetch = FetchType.LAZY)
    private List<Report> reports;

    @OneToMany(mappedBy = "reportedUser",fetch = FetchType.LAZY)
    private List<Report> reported;

    @OneToMany(mappedBy = "adminFollowedReport",fetch = FetchType.LAZY)
    private List<Report> adminFollowedMyReport;

    @OneToMany(mappedBy = "receiver",fetch = FetchType.LAZY)
    private List<Notification> notifications;

    public Boolean isAdministrator() {
        return role.equals(UserRole.ADMIN) || role.equals(UserRole.SUPER_ADMIN);
    }

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
        reviewsNumber++;
        reviewsTotalSum += vote;
    }

    public void removeReview(int vote) {
        reviewsNumber--;
        reviewsTotalSum -= vote;
    }

    public void editReview(int oldVote, int newVote) {
        reviewsTotalSum -= oldVote;
        reviewsTotalSum += newVote;
    }

    @Override
    public boolean isAccountNonExpired() {
        return !status.equals(UserStatus.CANCELLED);
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
