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
    private AddressDTO address;
    private String role;
    private PaymentMethodDTO defaultPaymentMethod;
    private List<PaymentMethodDTO> paymentMethods;
    private List<OfferDTO> offers;
    private List<UserDTO> followers;
    private List<ProductDTO> soldProducts;
    private List<UserDTO> follows;
    private List<ProductDTO> likes;
    private List<MessageDTO> sendMessages;
    private List<MessageDTO> receivedMessages;
    private List<OrderDTO> orders;
    private List<ReviewDTO> receivedReviews;
    private List<ReviewDTO> sentReviews;

}
