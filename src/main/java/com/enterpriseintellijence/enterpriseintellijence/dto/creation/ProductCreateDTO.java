package com.enterpriseintellijence.enterpriseintellijence.dto.creation;

import com.enterpriseintellijence.enterpriseintellijence.dto.*;
import com.enterpriseintellijence.enterpriseintellijence.dto.basics.UserBasicDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.Availability;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.Condition;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.ProductSize;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.Visibility;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@ToString
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = ClothingCreateDTO.class, name = "Clothing"),
        @JsonSubTypes.Type(value = EntertainmentCreateDTO.class, name = "Entertainment"),
        @JsonSubTypes.Type(value = HomeCreateDTO.class, name = "Home"),
        @JsonSubTypes.Type(value = ProductCreateDTO.class,name = "Other")
})
public class ProductCreateDTO {

    @NotNull
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

    @NotNull
    private Condition condition;

    @NotNull
    private ProductSize productSize;

    @NotNull
    private Visibility visibility;

    @NotNull
    private ProductCategoryDTO productCategory;

    private List<@Size(max = 5)MultipartFile> productImages;
}
