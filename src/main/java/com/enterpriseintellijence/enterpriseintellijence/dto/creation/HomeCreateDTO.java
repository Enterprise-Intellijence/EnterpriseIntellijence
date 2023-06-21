package com.enterpriseintellijence.enterpriseintellijence.dto.creation;

import com.enterpriseintellijence.enterpriseintellijence.dto.SizeDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.Colour;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.HomeMaterial;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@ToString
@Builder
@AllArgsConstructor
public class HomeCreateDTO {
    @NotNull
    private Colour colour;
    @NotNull
    private SizeDTO homeSize;
    @NotNull
    private HomeMaterial homeMaterial;
}
