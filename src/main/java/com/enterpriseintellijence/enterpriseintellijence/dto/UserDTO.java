package com.enterpriseintellijence.enterpriseintellijence.dto;

import com.enterpriseintellijence.enterpriseintellijence.dto.enums.Provider;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.UserRole;

import com.enterpriseintellijence.enterpriseintellijence.dto.enums.UserStatus;
import jakarta.validation.constraints.PositiveOrZero;
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

    @NotNull
    private String id;

    @Length(min = 3, max = 25)
    @NotNull
    private String username;

    @Email
    @NotNull
    private String email;

    @Length(max = 500)
    private String bio;

    private UserImageDTO photoProfile;

    @NotNull
    private Provider provider;

    @NotNull
    private UserStatus status;

    private List<AddressDTO> addresses;

    private List<PaymentMethodDTO> paymentMethods;

    @NotNull
    private UserRole role;

    @PositiveOrZero
    private int reviewsTotalSum;

    @PositiveOrZero
    private int reviewsNumber;

    @PositiveOrZero
    private Integer followersNumber;

    @PositiveOrZero
    private Integer followingNumber;

}
