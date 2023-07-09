package com.enterpriseintellijence.enterpriseintellijence.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ProductCategoryDTO {

    private String id;

    @NotNull
    private String primaryCat;

    @NotNull
    private String secondaryCat;

    @NotNull
    private String tertiaryCat;
}
