package com.enterpriseintellijence.enterpriseintellijence.dto;

import com.enterpriseintellijence.enterpriseintellijence.dto.basics.UserBasicDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.*;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

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

    private ProductCategoryDTO productCategory;


    private List<UserBasicDTO> usersThatLiked;


    private List<@Size(min = 1, max = 5) MultipartFile> productImages;

}
