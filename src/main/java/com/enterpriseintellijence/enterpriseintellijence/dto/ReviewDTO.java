package com.enterpriseintellijence.enterpriseintellijence.dto;

import com.enterpriseintellijence.enterpriseintellijence.data.entities.User;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString
public class ReviewDTO {

    private String id;
    private String title;
    private String description;
    @Max(5)
    @Min(1)
    private Integer vote;
    private UserDTO reviewer;
    private UserDTO reviewed;

}
