package com.enterpriseintellijence.enterpriseintellijence.dto;

import com.enterpriseintellijence.enterpriseintellijence.dto.enums.ClothingType;
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
    private String size;    // TODO: 01/05/2023 deve essere un enum 
    private String colour; // TODO: 01/05/2023 deve essere un enum 
    private ClothingType clothingType;

}
