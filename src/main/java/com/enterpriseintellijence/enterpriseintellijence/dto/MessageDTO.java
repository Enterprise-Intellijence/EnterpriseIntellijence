package com.enterpriseintellijence.enterpriseintellijence.dto;

import com.enterpriseintellijence.enterpriseintellijence.dto.basics.OfferBasicDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.basics.ProductBasicDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.basics.UserBasicDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.MessageStatus;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@ToString
@Builder
@AllArgsConstructor
public class MessageDTO {

    private String id;
    private String context;

    private LocalDateTime messageDate;
    private MessageStatus messageStatus;

    private ProductBasicDTO product;

    private UserBasicDTO sendUser;
    private UserBasicDTO receivedUser;

    private OfferBasicDTO offer;


}
