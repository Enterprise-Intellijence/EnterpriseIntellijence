package com.enterpriseintellijence.enterpriseintellijence.data.services;

import com.enterpriseintellijence.enterpriseintellijence.dto.DeliveryDTO;
import org.springframework.http.ResponseEntity;

public interface DeliveryService {

    public DeliveryDTO createDelivery(DeliveryDTO deliveryDTO);
    public DeliveryDTO replaceDelivery(String id, DeliveryDTO deliveryDTO) throws IllegalAccessException;
    public DeliveryDTO updateDelivery(String id, DeliveryDTO deliveryDTO);
    public void deleteDelivery(String id);
    public DeliveryDTO getDeliveryById(String id);
}
