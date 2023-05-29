package com.enterpriseintellijence.enterpriseintellijence.dto.creation;

import com.enterpriseintellijence.enterpriseintellijence.dto.AddressDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.CustomMoneyDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.basics.OrderBasicDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.DeliveryStatus;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@ToString
@Builder
@AllArgsConstructor
public class DeliveryCreateDTO {
    private OrderBasicDTO order;
    @Length(max = 50)
    private String shipper;
}
