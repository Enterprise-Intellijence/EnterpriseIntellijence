package com.enterpriseintellijence.enterpriseintellijence.data.services;

import com.enterpriseintellijence.enterpriseintellijence.dto.DeliveryDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class DeliveryServiceImp implements DeliveryService {

    @Override
    public DeliveryDTO createDelivery(DeliveryDTO deliveryDTO) {
        //TODO: Implement this method
        return null;
    }

    @Override
    public ResponseEntity<DeliveryDTO> replaceDelivery(String id, DeliveryDTO deliveryDTO) {
        //TODO: Implement this method
        return null;
    }

    @Override
    public ResponseEntity<DeliveryDTO> updateDelivery(String Id, DeliveryDTO deliveryDTO) {
        //TODO: Implement this method
        return null;
    }

    @Override
    public ResponseEntity<DeliveryDTO> deleteDelivery(String id) {
        //TODO: Implement this method
        return null;
    }

    @Override
    public ResponseEntity<DeliveryDTO> getDelivery(String id) {
        //TODO: Implement this method
        return null;
    }

    @Override
    public Iterable<DeliveryDTO> getAllDeliveries() {
        return null;
    }
}
