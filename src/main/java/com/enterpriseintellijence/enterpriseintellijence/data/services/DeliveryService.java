package com.enterpriseintellijence.enterpriseintellijence.data.services;

import com.enterpriseintellijence.enterpriseintellijence.dto.DeliveryDTO;

public interface DeliveryService {

    public DeliveryDTO createDelivery(DeliveryDTO deliveryDTO) throws IllegalAccessException;
    public DeliveryDTO replaceDelivery(String id, DeliveryDTO deliveryDTO) throws IllegalAccessException;
    public DeliveryDTO updateDelivery(String id, DeliveryDTO deliveryDTO) throws IllegalAccessException;
    public void deleteDelivery(String id) throws IllegalAccessException;
    public DeliveryDTO getDeliveryById(String id) throws IllegalAccessException;
}
