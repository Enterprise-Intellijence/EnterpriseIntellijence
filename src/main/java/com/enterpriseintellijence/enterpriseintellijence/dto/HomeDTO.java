package com.enterpriseintellijence.enterpriseintellijence.dto;

import com.enterpriseintellijence.enterpriseintellijence.dto.enums.Colour;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.HomeType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@ToString
public class HomeDTO extends ProductFullDTO {
    private Colour colour;
    private HomeType homeType;
}
