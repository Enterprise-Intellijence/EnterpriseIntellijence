package com.enterpriseintellijence.enterpriseintellijence.dto;

import com.enterpriseintellijence.enterpriseintellijence.dto.basics.UserBasicDTO;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@ToString
@Builder
@AllArgsConstructor
public class ReviewDTO {

    @NotNull
    private String id;

    @Past
    private LocalDateTime date;

    @NotNull
    @Length(max = 20)
    private String title;

    @Length(max = 250)
    private String description;

    @Max(5)
    @Min(1)
    @NotNull
    private Integer vote;

    @NotNull
    private UserBasicDTO reviewer;

    @NotNull
    private UserBasicDTO reviewed;

}
