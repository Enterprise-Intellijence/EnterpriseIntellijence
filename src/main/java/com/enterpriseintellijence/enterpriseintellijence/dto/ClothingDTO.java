package com.enterpriseintellijence.enterpriseintellijence.dto;

import com.enterpriseintellijence.enterpriseintellijence.dto.enums.ClothingSize;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.Colour;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.ProductGender;
import com.fasterxml.jackson.annotation.JsonSetter;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@ToString
public class ClothingDTO extends ProductDTO {
    @NotNull
    private ProductGender productGender;
    @NotNull
    private ClothingSize size;
    @NotNull
    private Colour colour;

    @JsonSetter("size")
    public void setClothingSize(ClothingSize size){
        if(!getProductCategoryParent().equals(size.getProductCategoryParent()))
            throw new IllegalArgumentException("size not valid");
        this.size = size;
        System.out.println(this.size.size);
    }

}
