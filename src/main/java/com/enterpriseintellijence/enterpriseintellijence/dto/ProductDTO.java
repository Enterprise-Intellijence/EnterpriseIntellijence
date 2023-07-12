package com.enterpriseintellijence.enterpriseintellijence.dto;

import com.enterpriseintellijence.enterpriseintellijence.dto.basics.UserBasicDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.*;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
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
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = ClothingDTO.class, name = "Clothing"),
        @JsonSubTypes.Type(value = EntertainmentDTO.class, name = "Entertainment"),
        @JsonSubTypes.Type(value = HomeDTO.class, name = "Home"),
        @JsonSubTypes.Type(value = ProductDTO.class, name = "Other")
})

public class ProductDTO {

    @NotNull
    private String id;

    @Length(max = 100)
    @NotNull
    private String title;

    @Length(max = 1000)
    private String description;

    @NotNull
    private CustomMoneyDTO productCost;

    @NotNull
    private CustomMoneyDTO deliveryCost;

    @Length(max = 100)
    private String brand;

    @NotNull
    private Condition condition;

    @PositiveOrZero
    private Integer likesNumber;

    @NotNull
    private UserBasicDTO seller;

    @NotNull
    private ProductSize productSize;

    @PositiveOrZero
    private Integer views;

    @Past
    private LocalDateTime uploadDate;

    @Past
    private LocalDateTime lastUpdateDate;

    @NotNull
    private Visibility visibility;

    @NotNull
    private Availability availability;

    @NotNull
    private ProductCategoryDTO productCategory;

    private List<UserBasicDTO> usersThatLiked;

    private List<ProductImageDTO> productImages;

}
