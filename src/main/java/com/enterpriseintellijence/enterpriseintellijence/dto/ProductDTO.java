package com.enterpriseintellijence.enterpriseintellijence.dto;

import com.enterpriseintellijence.enterpriseintellijence.dto.enums.Availability;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.Condition;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.ProductSize;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.Visibility;

import org.hibernate.validator.constraints.Length;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @see com.enterpriseintellijence.enterpriseintellijence.data.entities.Product
 */
@Data
@NoArgsConstructor
@ToString
public class ProductDTO {

    private String id;

    @Length(max = 100)
    private String title;

    @Length(max = 1000)
    private String description;

    // TODO: usare una classe apposita per il prezzo
    private Float price;

    @Length(max = 100)
    private String brand;
    private Condition condition;
    private AddressDTO address;
    private ProductSize productSize;
    private Integer views;
    private LocalDateTime uploadDate;
    private Visibility visibility;
    private Availability availability;
    private UserDTO seller;
    private List<UserDTO> usersThatLiked;
    private List<OfferDTO> offers;
    private List<MessageDTO> messages;
    private OrderDTO order;

}
