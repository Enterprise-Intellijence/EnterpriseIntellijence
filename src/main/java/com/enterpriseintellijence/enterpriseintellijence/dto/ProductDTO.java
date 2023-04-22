package com.enterpriseintellijence.enterpriseintellijence.dto;

import com.enterpriseintellijence.enterpriseintellijence.data.entities.Message;
import com.enterpriseintellijence.enterpriseintellijence.data.entities.Offer;
import com.enterpriseintellijence.enterpriseintellijence.data.entities.Order;
import com.enterpriseintellijence.enterpriseintellijence.data.entities.User;
import com.enterpriseintellijence.enterpriseintellijence.data.entities.embedded.Address;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
    private AddressDTO address;
    private String deliveryType;
    private Integer views;
    private LocalDateTime uploadDate;
    private String visibility;
    private Boolean availability;
    private LocalDateTime sendDate;
    private UserDTO seller;
    private List<UserDTO> users;
    private List<OfferDTO> offers;
    private List<MessageDTO> messages;
    private OrderDTO order;

}
