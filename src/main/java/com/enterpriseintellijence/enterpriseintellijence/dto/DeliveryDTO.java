package com.enterpriseintellijence.enterpriseintellijence.dto;
import com.enterpriseintellijence.enterpriseintellijence.data.entities.Order;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString
public class DeliveryDTO {

    private String Id;
    private Order order;
    private Float deliveryCost;
    private String shipper;

    //TODO: da vedere indirizzo mittente e destinatario

}
