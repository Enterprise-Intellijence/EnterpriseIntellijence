package com.enterpriseintellijence.enterpriseintellijence.data.services;

import com.enterpriseintellijence.enterpriseintellijence.dto.DeliveryDTO;
import org.springframework.http.ResponseEntity;

public interface DeliveryService {

    public DeliveryDTO createDelivery(DeliveryDTO deliveryDTO);
    public ResponseEntity<DeliveryDTO> replaceDelivery(String id, DeliveryDTO deliveryDTO);
    public ResponseEntity<DeliveryDTO> updateDelivery(DeliveryDTO deliveryDTO);
    ResponseEntity<DeliveryDTO> deleteDelivery(String id);
    ResponseEntity<DeliveryDTO> getDelivery(String id);
    Iterable<DeliveryDTO> getAllDeliveries();

}
