package com.enterpriseintellijence.enterpriseintellijence.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@ToString
public class WearableDTO extends ProductDTO {

    private String size;
    private String colour; //TODO: da vedere se utilizzare o meno un ENUM
    private String type;

}
