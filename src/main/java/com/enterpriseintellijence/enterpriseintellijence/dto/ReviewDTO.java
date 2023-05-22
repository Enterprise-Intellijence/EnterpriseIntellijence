package com.enterpriseintellijence.enterpriseintellijence.dto;

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
public class ReviewDTO {

    private String id;
    private LocalDateTime date;
    private String title;
    private String description;
    @Max(5)
    @Min(1)
    private Integer vote;
    private UserBasicDTO reviewer;
    private UserBasicDTO reviewed;

}
