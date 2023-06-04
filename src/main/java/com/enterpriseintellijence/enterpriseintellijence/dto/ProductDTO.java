package com.enterpriseintellijence.enterpriseintellijence.dto;

import com.enterpriseintellijence.enterpriseintellijence.dto.basics.UserBasicDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.*;
import com.fasterxml.jackson.annotation.JsonSetter;
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
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "productCategory", visible = true, defaultImpl = ProductDTO.class)
@JsonSubTypes({
        @JsonSubTypes.Type(value = ClothingDTO.class, name = "CLOTHING"),
        @JsonSubTypes.Type(value = EntertainmentDTO.class, name = "ENTERTAINMENT"),
        @JsonSubTypes.Type(value = HomeDTO.class, name = "HOME"),
        @JsonSubTypes.Type(value = ProductDTO.class, name = "OTHER")
})

public class ProductDTO {
    private String id;

    @Length(max = 100)
    private String title;

    @Length(max = 1000)
    private String description;

    @NotNull
    private CustomMoneyDTO productCost;

    @NotNull
    private CustomMoneyDTO deliveryCost;

    @Length(max = 100)
    private String brand;
    private Condition condition;
    private Integer likesNumber;
    private UserBasicDTO seller;


    //private AddressDTO address;
    private ProductSize productSize;

    @PositiveOrZero
    private Integer views;
    private LocalDateTime uploadDate;
    private LocalDateTime lastUpdateDate;

    private Visibility visibility;
    private Availability availability;

    @NotNull
    private ProductCategoryChild productCategoryChild;

    private ProductCategoryParent productCategoryParent;

    @NotNull
    private ProductCategory productCategory;

    private List<UserBasicDTO> usersThatLiked;


    private List<ProductImageDTO> productImages;

    @JsonSetter("productCategoryChild")
    public void setProductCategoryChild(ProductCategoryChild productCategoryChild) {
        this.productCategoryChild = productCategoryChild;
        this.productCategoryParent = productCategoryChild.getSubCategoryType();
        ProductCategory temp = productCategoryChild.getSubCategoryType().getProductCategory();
        if (!temp.equals(productCategory))
            throw new IllegalArgumentException("Error of main category and sub category");
        this.productCategory = temp;
    }
}
