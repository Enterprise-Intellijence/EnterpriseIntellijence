package com.enterpriseintellijence.enterpriseintellijence.dto;

import com.enterpriseintellijence.enterpriseintellijence.dto.basics.OfferBasicDTO;
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
public class MessageDTO {

    private String id;
    @NotNull
    @Length(max = 250)
    private String context;

    private LocalDateTime messageDate;
    private MessageStatus messageStatus;


    private ProductBasicDTO product;

    @NotNull
    private UserBasicDTO sendUser;
    @NotNull
    private UserBasicDTO receivedUser;

    private OfferBasicDTO offer;


}
