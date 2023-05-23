package com.enterpriseintellijence.enterpriseintellijence.dto;

import com.enterpriseintellijence.enterpriseintellijence.data.entities.UserImage;
import com.enterpriseintellijence.enterpriseintellijence.dto.basics.PaymentMethodBasicDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.basics.ProductBasicDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.Provider;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.UserRole;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;
import java.util.Set;

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

    @Length(min = 8, max = 20)
    private String password;
    @Email
    private String email;

    private UserImageDTO photoProfile;
    @NotNull
    private Provider provider;
    private AddressDTO address;
    @NotNull
    private UserRole role;

    private PaymentMethodBasicDTO defaultPaymentMethod;

    private Integer followers_number;
    private Integer following_number;

    private List<ReviewDTO> receivedReviews;
    private List<ReviewDTO> sentReviews;

    /*esiste il metodo per fare la get su questo
    private List<ProductBasicDTO> sellingProducts; FATTO */

    /*
    private List<PaymentMethodBasicDTO> paymentMethods FATTO;
    //private List<OfferDTO> offers;
    private List<UserDTO> followers FATTO;
    private List<UserDTO> follows FATTO;
    private List<ProductBasicDTO> likes; FATTO
    private List<MessageDTO> sendMessages FATTO;
    private List<MessageDTO> receivedMessages FATTO;
    private List<OrderDTO> orders; FATTO

*/

}
