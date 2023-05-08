package com.enterpriseintellijence.enterpriseintellijence.dto;

import com.enterpriseintellijence.enterpriseintellijence.data.entities.Message;
import com.enterpriseintellijence.enterpriseintellijence.data.entities.Order;
import com.enterpriseintellijence.enterpriseintellijence.data.entities.Product;
import com.enterpriseintellijence.enterpriseintellijence.data.entities.User;
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
    private Money amount;

    // TODO:
    private OfferState state;
    private UserDTO offerer;
    private ProductDTO product;
    private MessageDTO message;
    private OrderDTO order;

}
