package com.enterpriseintellijence.enterpriseintellijence.dto;

import com.enterpriseintellijence.enterpriseintellijence.dto.basics.UserBasicDTO;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@ToString
@Builder
@AllArgsConstructor
public class FollowingFollowersDTO {

    private String id;

    @Past
    private LocalDateTime followingFrom;

    @NotNull
    private UserBasicDTO follower;

    @NotNull
    private UserBasicDTO following;
}
