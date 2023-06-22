package com.enterpriseintellijence.enterpriseintellijence.dto.creation;

import com.enterpriseintellijence.enterpriseintellijence.dto.SizeDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.Colour;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.ProductGender;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@EqualsAndHashCode(callSuper = true)

@Data
@NoArgsConstructor
@ToString
public class ClothingCreateDTO extends ProductCreateDTO {
    @NotNull
    private ProductGender productGender;
    @NotNull
    private SizeDTO clothingSize;
    @NotNull
    private Colour colour;
}
