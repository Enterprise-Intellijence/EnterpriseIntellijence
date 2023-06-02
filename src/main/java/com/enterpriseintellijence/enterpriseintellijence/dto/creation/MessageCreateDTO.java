package com.enterpriseintellijence.enterpriseintellijence.dto.creation;

import com.enterpriseintellijence.enterpriseintellijence.dto.basics.ProductBasicDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.basics.UserBasicDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.MessageStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;
@Data
@NoArgsConstructor
@ToString
@Builder
@AllArgsConstructor
public class MessageCreateDTO {
    @NotNull
    @Length(max = 500)
    private String text;

    private ProductBasicDTO product;

    @NotNull
    private UserBasicDTO receivedUser;

}
