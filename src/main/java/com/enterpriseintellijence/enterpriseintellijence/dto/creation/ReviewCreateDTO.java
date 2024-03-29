package com.enterpriseintellijence.enterpriseintellijence.dto.creation;

import com.enterpriseintellijence.enterpriseintellijence.dto.basics.UserBasicDTO;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.validator.constraints.Length;

@Data
@NoArgsConstructor
@ToString
@Builder
@AllArgsConstructor
public class ReviewCreateDTO {

    @NotNull
    private String orderId;

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
    private UserBasicDTO reviewed;
}
