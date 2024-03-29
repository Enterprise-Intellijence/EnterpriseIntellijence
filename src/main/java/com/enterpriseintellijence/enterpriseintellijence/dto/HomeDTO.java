package com.enterpriseintellijence.enterpriseintellijence.dto;

import com.enterpriseintellijence.enterpriseintellijence.dto.enums.Colour;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.HomeMaterial;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@ToString
public class HomeDTO extends ProductDTO {
    @NotNull
    private Colour colour;
    @NotNull
    private SizeDTO homeSize;
    @NotNull
    private HomeMaterial homeMaterial;
}
