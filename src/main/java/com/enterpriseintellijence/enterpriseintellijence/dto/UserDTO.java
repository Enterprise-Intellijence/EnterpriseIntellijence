package com.enterpriseintellijence.enterpriseintellijence.dto;

import com.enterpriseintellijence.enterpriseintellijence.data.entities.*;
import com.enterpriseintellijence.enterpriseintellijence.data.entities.embedded.Address;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import java.util.List;

@Data
@NoArgsConstructor
@ToString
public class UserDTO {

    private String id;
    private String username;
    private String password;
    private String email;
    private byte[] photo;
    private Address address;
    private String role;
    private PaymentMethod defaultPaymentMethod;
    private List<PaymentMethod> paymentMethods;
    private List<Offer> offers;
    private List<com.enterpriseintellijence.enterpriseintellijence.data.entities.User> followers;
    private List<Product> soldProducts;
    private List<com.enterpriseintellijence.enterpriseintellijence.data.entities.User> follows;
    private List<Product> likes;
    private List<Message> sendMessages;
    private List<Message> receivedMessages;
    private List<Order> orders;
    private List<Review> receivedReviews;
    private List<Review> sentReviews;

}
