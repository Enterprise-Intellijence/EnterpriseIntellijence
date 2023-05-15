package com.enterpriseintellijence.enterpriseintellijence.dto;

import lombok.*;

@Data
@NoArgsConstructor
@ToString
@Builder
@AllArgsConstructor
public class MessageDTO {

    private String id;
    private String context;
    private ProductBasicDTO product;
    private UserDTO sendUser;
    private UserDTO receivedUser;
    private OfferDTO offer;

}
