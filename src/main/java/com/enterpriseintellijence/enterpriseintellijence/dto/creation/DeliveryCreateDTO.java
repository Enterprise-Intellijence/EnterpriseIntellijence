package com.enterpriseintellijence.enterpriseintellijence.dto.creation;

import com.enterpriseintellijence.enterpriseintellijence.dto.basics.OrderBasicDTO;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.validator.constraints.Length;

@Data
@NoArgsConstructor
@ToString
@Builder
@AllArgsConstructor
public class DeliveryCreateDTO {
    @NotNull
    private OrderBasicDTO order;

    @Length(max = 50)
    private String shipper;

    @NotNull
    private String senderAddressId;
}
