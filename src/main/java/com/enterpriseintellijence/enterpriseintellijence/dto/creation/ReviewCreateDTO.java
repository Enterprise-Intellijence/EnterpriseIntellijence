package com.enterpriseintellijence.enterpriseintellijence.dto.creation;

import com.enterpriseintellijence.enterpriseintellijence.dto.basics.OrderBasicDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.basics.UserBasicDTO;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@ToString
@Builder
@AllArgsConstructor
public class ReviewCreateDTO {
    private OrderBasicDTO orderBasicDTO;
    private String title;
    private String description;
    @Max(5)
    @Min(1)
    private Integer vote;
    private UserBasicDTO reviewed;
}
