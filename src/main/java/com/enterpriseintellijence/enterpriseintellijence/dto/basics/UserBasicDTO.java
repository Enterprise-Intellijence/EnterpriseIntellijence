package com.enterpriseintellijence.enterpriseintellijence.dto.basics;


import com.enterpriseintellijence.enterpriseintellijence.data.entities.UserImage;
import com.enterpriseintellijence.enterpriseintellijence.dto.UserImageDTO;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
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

    private String bio;

    private UserImageDTO photoProfile;
    private int reviewsTotalSum;
    private int reviewsNumber;

    private int followersNumber;
    private int followingNumber;

}
