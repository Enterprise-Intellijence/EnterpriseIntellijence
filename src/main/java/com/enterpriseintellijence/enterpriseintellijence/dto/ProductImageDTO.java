package com.enterpriseintellijence.enterpriseintellijence.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ProductImageDTO {
    private String id;
    private byte[] photo;
    private ProductBasicDTO productBasicDTO;
}
