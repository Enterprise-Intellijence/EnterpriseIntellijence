package com.enterpriseintellijence.enterpriseintellijence.dto;

import com.enterpriseintellijence.enterpriseintellijence.dto.basics.OrderBasicDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.basics.ProductBasicDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.basics.UserBasicDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.OfferState;

import jakarta.persistence.Column;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@ToString
@Builder
@AllArgsConstructor
public class OfferDTO {

    private String id;

    private CustomMoneyDTO amount;

    private LocalDateTime creationTime;

    // TODO:
    private OfferState state;
    private UserBasicDTO offerer;
    private ProductBasicDTO product;
    private OrderBasicDTO order;

}
