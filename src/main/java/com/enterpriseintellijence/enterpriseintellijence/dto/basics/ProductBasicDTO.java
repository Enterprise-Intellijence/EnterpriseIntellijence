package com.enterpriseintellijence.enterpriseintellijence.dto.basics;

import com.enterpriseintellijence.enterpriseintellijence.dto.ProductCategoryDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.CustomMoneyDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.ProductImageDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.*;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@ToString
@AllArgsConstructor
@Builder
/*@JsonTypeInfo(
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "productCategory",
        use = JsonTypeInfo.Id.NAME,
        visible = true
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = ProductBasicDTO.class, name = "OTHER"),
        @JsonSubTypes.Type(value = ClothingDTO.class, name = "CLOTHING"),
        @JsonSubTypes.Type(value = HomeDTO.class, name = "HOME"),
        @JsonSubTypes.Type(value = EntertainmentDTO.class, name = "ENTERTAINMENT")

})*/
public class ProductBasicDTO {

    private String id;

    @Length(max = 100)
    private String title;

    @Length(max = 1000)
    private String description;
    private LocalDateTime uploadDate;

    @NotNull
    private CustomMoneyDTO productCost;

    @NotNull
    private CustomMoneyDTO deliveryCost;

    @Length(max = 100)
    private String brand;
    private Condition condition;
    // TODO: 16/05/2023 mappare sul mappare
    private Integer likesNumber;
    private UserBasicDTO seller;
    private ProductImageDTO productImages;
/*    private ProductCategoryOld productCategory;*/
    private ProductCategoryDTO productCategory;
/*

    private AddressDTO address;
    private ProductSize productSize;

    @PositiveOrZero
    private Integer views;
    private LocalDateTime uploadDate;
    private Visibility visibility;
    private Availability availability;


    private List<UserDTO> usersThatLiked;
    private List<OfferDTO> offers;
    private List<MessageDTO> messages;
    private OrderDTO order;
    private List<ProductImageDTO> productImages;
*/

}
