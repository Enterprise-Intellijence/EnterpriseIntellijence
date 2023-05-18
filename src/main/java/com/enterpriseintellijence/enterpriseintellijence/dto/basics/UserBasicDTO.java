package com.enterpriseintellijence.enterpriseintellijence.dto.basics;


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

    private byte[] photo;


}
