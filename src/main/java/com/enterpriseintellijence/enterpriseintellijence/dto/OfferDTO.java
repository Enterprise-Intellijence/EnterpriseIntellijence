package com.enterpriseintellijence.enterpriseintellijence.dto;

import com.enterpriseintellijence.enterpriseintellijence.data.entities.Message;
import com.enterpriseintellijence.enterpriseintellijence.data.entities.Order;
import com.enterpriseintellijence.enterpriseintellijence.data.entities.Product;
import com.enterpriseintellijence.enterpriseintellijence.data.entities.User;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.OfferState;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString
public class OfferDTO {

    private String id;

    // TODO: usare classe specifica per i soldi
    private Float amount;

    // TODO:
    private OfferState state;
    private UserDTO offerer;
    private ProductDTO product;
    private MessageDTO message;
    private OrderDTO order;

}
