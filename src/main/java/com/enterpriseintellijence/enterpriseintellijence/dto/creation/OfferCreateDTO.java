package com.enterpriseintellijence.enterpriseintellijence.dto.creation;

import com.enterpriseintellijence.enterpriseintellijence.dto.CustomMoneyDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.basics.OrderBasicDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.basics.ProductBasicDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.basics.UserBasicDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.OfferState;
import lombok.*;

import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@ToString
@Builder
@AllArgsConstructor
public class OfferCreateDTO {

    private CustomMoneyDTO amount;
    private ProductBasicDTO product;
}
