package com.enterpriseintellijence.enterpriseintellijence.dto;

import com.enterpriseintellijence.enterpriseintellijence.data.entities.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

@Data
@NoArgsConstructor
@ToString
@AllArgsConstructor
@Builder
public class UserImageDTO {
    private String id;
    private String description;
    private String urlPhoto;

}
