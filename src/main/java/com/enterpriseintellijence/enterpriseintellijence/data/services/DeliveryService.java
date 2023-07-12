package com.enterpriseintellijence.enterpriseintellijence.data.services;

import com.enterpriseintellijence.enterpriseintellijence.dto.AddressDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.DeliveryDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.creation.AddressCreateDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.creation.DeliveryCreateDTO;

public interface DeliveryService {

    DeliveryDTO createDelivery(DeliveryCreateDTO deliveryDTO) throws IllegalAccessException;
    DeliveryDTO updateDelivery(String id, DeliveryDTO deliveryDTO) throws IllegalAccessException;
    DeliveryDTO getDeliveryById(String id) throws IllegalAccessException;

    AddressDTO createAddress(AddressCreateDTO addressCreateDTO);

    AddressDTO updateAddress(String id, AddressDTO addressDTO) throws IllegalAccessException;

    void deleteAddress(String id) throws IllegalAccessException;

    AddressDTO getAddress(String id);

    Iterable<AddressDTO> getMyAddressList() throws IllegalAccessException;

}
