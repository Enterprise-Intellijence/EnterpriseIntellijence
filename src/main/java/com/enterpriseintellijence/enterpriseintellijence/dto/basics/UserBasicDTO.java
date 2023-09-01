package com.enterpriseintellijence.enterpriseintellijence.dto.basics;


import com.enterpriseintellijence.enterpriseintellijence.dto.UserImageDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.UserStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import org.hibernate.validator.constraints.Length;

@Data
@NoArgsConstructor
@ToString
@AllArgsConstructor
@Builder
public class UserBasicDTO {

    @NotNull
    private String id;

    @Length(min = 3, max = 25)
    @NotNull
    private String username;

    @Length(max = 500)
    private String bio;

    private UserImageDTO photoProfile;
    private Integer reviewsTotalSum;
    private Integer reviewsNumber;

    private Integer followersNumber;
    private Integer followingNumber;

    private UserStatus status;

}
