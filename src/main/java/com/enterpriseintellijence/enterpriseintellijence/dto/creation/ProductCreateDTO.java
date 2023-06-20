package com.enterpriseintellijence.enterpriseintellijence.dto.creation;

import com.enterpriseintellijence.enterpriseintellijence.dto.CustomMoneyDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.ProductCategoryDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.ProductImageDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.basics.UserBasicDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.Availability;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.Condition;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.ProductSize;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.Visibility;
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
@Builder
@AllArgsConstructor
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
