package com.enterpriseintellijence.enterpriseintellijence.dto;

import com.enterpriseintellijence.enterpriseintellijence.data.entities.Message;
import com.enterpriseintellijence.enterpriseintellijence.data.entities.Offer;
import com.enterpriseintellijence.enterpriseintellijence.data.entities.Order;
import com.enterpriseintellijence.enterpriseintellijence.data.entities.User;
import com.enterpriseintellijence.enterpriseintellijence.data.entities.embedded.Address;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@ToString
public class ProductDTO {

    private String id;
    private String description;
    private Float price;
    private String brand;
    private String condition;
    private Address address;
    private String deliveryType;
    private Integer views;
    private LocalDate uploadDate;
    private String visibility;
    private Boolean availability;
    private LocalDate sendDate;
    private User seller;
    private List<User> users;
    private List<Offer> offers;
    private List<Message> messages;
    private Order order;

}