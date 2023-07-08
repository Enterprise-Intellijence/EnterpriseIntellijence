package com.enterpriseintellijence.enterpriseintellijence.dto;

import com.enterpriseintellijence.enterpriseintellijence.dto.enums.Provider;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.UserRole;

import com.enterpriseintellijence.enterpriseintellijence.dto.enums.UserStatus;
import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@NoArgsConstructor
@ToString
@AllArgsConstructor
@Builder
public class UserDTO {

    private String id;

    @Length(min = 3, max = 25)
    @NotNull
    private String username;

    @Email
    private String email;

    private String bio;

    private UserImageDTO photoProfile;

    @NotNull
    private Provider provider;

    private UserStatus status;

    private List<AddressDTO> addresses;
    private List<PaymentMethodDTO> paymentMethods;
    @NotNull
    private UserRole role;

    //private PaymentMethodBasicDTO defaultPaymentMethod;

    private int reviewsTotalSum;
    private int reviewsNumber;

    private Integer followersNumber;
    private Integer followingNumber;

    //private List<ReviewDTO> receivedReviews;
    //private List<ReviewDTO> sentReviews;

    /*esiste il metodo per fare la get su questo
    private List<ProductBasicDTO> sellingProducts; FATTO */

    /*
    private List<PaymentMethodBasicDTO> paymentMethods FATTO;
    //private List<OfferDTO> offers; FATTO
    private List<UserDTO> followers FATTO;
    private List<UserDTO> follows FATTO;
    private List<ProductBasicDTO> likes; FATTO
    private List<MessageDTO> sendMessages FATTO;
    private List<MessageDTO> receivedMessages FATTO;
    private List<OrderDTO> orders; FATTO

*/

}
