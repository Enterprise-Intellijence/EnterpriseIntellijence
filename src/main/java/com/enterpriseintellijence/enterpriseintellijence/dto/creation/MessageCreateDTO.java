package com.enterpriseintellijence.enterpriseintellijence.dto.creation;

import com.enterpriseintellijence.enterpriseintellijence.dto.basics.ProductBasicDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.basics.UserBasicDTO;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.validator.constraints.Length;

@Data
@NoArgsConstructor
@ToString
@Builder
@AllArgsConstructor
public class MessageCreateDTO {

    private String conversationId;

    @NotNull
    @Length(max = 500)
    private String text;

    private ProductBasicDTO product;

    @NotNull
    private UserBasicDTO receivedUser;

}
