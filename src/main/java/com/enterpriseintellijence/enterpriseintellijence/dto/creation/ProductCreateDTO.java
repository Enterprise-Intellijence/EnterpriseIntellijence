package com.enterpriseintellijence.enterpriseintellijence.dto.creation;

import com.enterpriseintellijence.enterpriseintellijence.dto.*;
import com.enterpriseintellijence.enterpriseintellijence.dto.basics.UserBasicDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.Availability;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.Condition;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.ProductSize;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.Visibility;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@ToString
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "productCategory", visible = true, defaultImpl = ProductCreateDTO.class)
@JsonSubTypes({
        @JsonSubTypes.Type(value = ClothingCreateDTO.class, name = "Clothing"),
        @JsonSubTypes.Type(value = EntertainmentCreateDTO.class, name = "Entertainment"),
        @JsonSubTypes.Type(value = HomeCreateDTO.class, name = "Home"),
})
public class ProductCreateDTO {

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

    // TODO: 19/06/2023 modificare nome
    private ProductSize productSize;

    private Visibility visibility;

    private ProductCategoryDTO productCategory;

    @Max(5)
    @Min(1)
    private List<MultipartFile> productImages;
}
