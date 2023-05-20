package com.enterpriseintellijence.enterpriseintellijence.dto;

import com.enterpriseintellijence.enterpriseintellijence.data.entities.ProductImage;
import com.enterpriseintellijence.enterpriseintellijence.data.entities.embedded.CustomMoney;
import com.enterpriseintellijence.enterpriseintellijence.dto.basics.UserBasicDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.*;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;
import java.util.List;


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
        @JsonSubTypes.Type(value = ClothingDTO.class, name = "CLOTHING"),
        @JsonSubTypes.Type(value = HomeDTO.class, name = "HOME"),
        @JsonSubTypes.Type(value = EntertainmentDTO.class, name = "ENTERTAINMENT")

})
public class ProductDTO {
    private String id;

    @Length(max = 100)
    private String title;

    @Length(max = 1000)
    private String description;

    // TODO: usare una classe apposita per il prezzo
    @NotNull
    private CustomMoney customMoney;

    @Length(max = 100)
    private String brand;
    private Condition condition;
    private Integer likesNumber;
    private UserBasicDTO seller;


    private AddressDTO address;
    private ProductSize productSize;

    @PositiveOrZero
    private Integer views;
    private LocalDateTime uploadDate;
    private Visibility visibility;
    private Availability availability;
    private ProductCategory productCategory;

    private List<UserBasicDTO> usersThatLiked;
    private List<OfferDTO> offers;
    private List<MessageDTO> messages;
    private OrderDTO order;
    private ProductImageDTO defaultImage;
    private List<ProductImageDTO> productImages;
}
