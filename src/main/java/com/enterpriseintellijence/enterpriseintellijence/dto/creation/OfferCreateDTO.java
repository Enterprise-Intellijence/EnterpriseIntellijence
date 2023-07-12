package com.enterpriseintellijence.enterpriseintellijence.dto.creation;

import com.enterpriseintellijence.enterpriseintellijence.dto.CustomMoneyDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.basics.ProductBasicDTO;
import jakarta.validation.constraints.NotNull;
import lombok.*;


@Data
@NoArgsConstructor
@ToString
@Builder
@AllArgsConstructor
public class OfferCreateDTO {

    @NotNull
    private CustomMoneyDTO amount;

    @NotNull
    private ProductBasicDTO product;
}
