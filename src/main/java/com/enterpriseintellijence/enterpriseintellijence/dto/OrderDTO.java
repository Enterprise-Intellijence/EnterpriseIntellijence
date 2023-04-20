package com.enterpriseintellijence.enterpriseintellijence.dto;

import com.enterpriseintellijence.enterpriseintellijence.data.entities.Delivery;
import com.enterpriseintellijence.enterpriseintellijence.data.entities.Offer;
import com.enterpriseintellijence.enterpriseintellijence.data.entities.Product;
import com.enterpriseintellijence.enterpriseintellijence.data.entities.User;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString
public class OrderDTO {

    private String id;
    private String state;
    private Product product;
    private User user;
    private Delivery delivery;
    private Offer offer;

}
