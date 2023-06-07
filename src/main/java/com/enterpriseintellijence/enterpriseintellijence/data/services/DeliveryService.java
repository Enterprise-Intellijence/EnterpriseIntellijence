package com.enterpriseintellijence.enterpriseintellijence.data.services;

import com.enterpriseintellijence.enterpriseintellijence.dto.AddressDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.DeliveryDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.creation.AddressCreateDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.creation.DeliveryCreateDTO;

public interface DeliveryService {

    public DeliveryDTO createDelivery(DeliveryCreateDTO deliveryDTO) throws IllegalAccessException;
    public DeliveryDTO replaceDelivery(String id, DeliveryDTO deliveryDTO) throws IllegalAccessException;
    public DeliveryDTO updateDelivery(String id, DeliveryDTO deliveryDTO) throws IllegalAccessException;
    public void deleteDelivery(String id) throws IllegalAccessException;
    public DeliveryDTO getDeliveryById(String id) throws IllegalAccessException;

    AddressDTO createAddress(AddressCreateDTO addressCreateDTO);

    AddressDTO replaceAddress(String id, AddressDTO addressDTO) throws IllegalAccessException;

    AddressDTO updateAddress(String id, AddressDTO addressDTO) throws IllegalAccessException;

    void deleteAddress(String id) throws IllegalAccessException;

    AddressDTO getAddress(String id);

    Iterable<AddressDTO> getMyAddressList() throws IllegalAccessException;

}
