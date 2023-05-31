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
    private String id;

    @Length(min = 3, max = 25)
    @NotNull
    private String username;

    private UserImageDTO photoProfile;
    private int reviews_total_sum;
    private int reviews_number;

    private int followers_number;
    private int following_number;

}
