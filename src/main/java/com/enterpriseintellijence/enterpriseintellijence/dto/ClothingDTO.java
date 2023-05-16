package com.enterpriseintellijence.enterpriseintellijence.dto;

import com.enterpriseintellijence.enterpriseintellijence.dto.enums.ClothingSize;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.ClothingType;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.Colour;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.ProductGender;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@ToString
public class ClothingDTO extends ProductDTO {

    private ProductGender productGender;
    private ClothingSize size;
    private Colour colour;
    private ClothingType clothingType;

}
