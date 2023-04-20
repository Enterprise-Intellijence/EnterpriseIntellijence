package com.enterpriseintellijence.enterpriseintellijence.dto;

import com.enterpriseintellijence.enterpriseintellijence.data.entities.User;
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
    private Integer vote;
    private User reviewer;
    private User reviewed;

}
