package com.enterpriseintellijence.enterpriseintellijence.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SizeDTO {
    @NotNull
    private String id;
    @NotNull
    private String sizeName;
    @NotNull
    private String type;
}
