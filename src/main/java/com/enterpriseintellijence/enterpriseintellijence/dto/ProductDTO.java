package com.enterpriseintellijence.enterpriseintellijence.dto;

import com.enterpriseintellijence.enterpriseintellijence.dto.enums.*;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
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
@JsonTypeInfo(
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "productCategory",
        use = JsonTypeInfo.Id.NAME,
        visible = true
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = ProductDTO.class, name = "OTHER"),
        @JsonSubTypes.Type(value = ClothingDTO.class, name = "CLOTHING")
})
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
    private ProductCategory productCategory;
    private UserDTO seller;
    private List<UserDTO> usersThatLiked;
    private List<OfferDTO> offers;
    private List<MessageDTO> messages;
    private OrderDTO order;

}
