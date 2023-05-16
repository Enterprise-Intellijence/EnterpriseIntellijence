package com.enterpriseintellijence.enterpriseintellijence.dto;

import com.enterpriseintellijence.enterpriseintellijence.dto.enums.OfferState;

import lombok.*;
import org.joda.money.Money;

@Data
@NoArgsConstructor
@ToString
@Builder
@AllArgsConstructor
public class OfferDTO {

    private String id;

    // TODO: usare classe specifica per i soldi
    private CustomMoneyDTO amount;

    // TODO:
    private OfferState state;
    private UserBasicDTO offerer;
    private ProductBasicDTO product;
    private MessageDTO message;
    private OrderDTO order;

}
