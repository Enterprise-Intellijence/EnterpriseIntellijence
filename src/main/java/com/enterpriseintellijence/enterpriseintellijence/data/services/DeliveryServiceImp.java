package com.enterpriseintellijence.enterpriseintellijence.data.services;

import com.enterpriseintellijence.enterpriseintellijence.data.entities.Delivery;
import com.enterpriseintellijence.enterpriseintellijence.data.entities.Order;
import com.enterpriseintellijence.enterpriseintellijence.data.repository.DeliveryRepository;
import com.enterpriseintellijence.enterpriseintellijence.dto.DeliveryDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.OrderDTO;
import com.enterpriseintellijence.enterpriseintellijence.exception.IdMismatchException;
import lombok.RequiredArgsConstructor;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeliveryServiceImp implements DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final ModelMapper modelMapper;

    @Override
    public DeliveryDTO createDelivery(DeliveryDTO deliveryDTO) {
        Delivery delivery = mapToEntity(deliveryDTO);

        delivery.setOrder(mapToEntity(deliveryDTO.getOrder()));
        delivery.setDeliveryCost(deliveryDTO.getDeliveryCost());
        delivery.setShipper(deliveryDTO.getShipper());

        deliveryRepository.save(delivery);
        return mapToDTO(delivery);
    }

    @Override
    public DeliveryDTO replaceDelivery(String id, DeliveryDTO deliveryDTO) throws IllegalAccessException {
        throwOnIdMismatch(id, deliveryDTO);

        Delivery oldDelivery = deliveryRepository.findById(Long.valueOf(id)).orElseThrow(EntityNotFoundException::new);
        Delivery newDelivery = mapToEntity(deliveryDTO);

        if (!oldDelivery.getOrder().getId().equals(newDelivery.getOrder().getId())) {
            throw new IllegalAccessException("Can't change delivery");
        }

        newDelivery = deliveryRepository.save(newDelivery);
        return mapToDTO(newDelivery);
    }

    @Override
    public DeliveryDTO updateDelivery(String Id, DeliveryDTO patch) {
        throwOnIdMismatch(Id, patch);
        DeliveryDTO deliveryDTO = mapToDTO(deliveryRepository.findById(Long.valueOf(Id)).orElseThrow(EntityNotFoundException::new));

        if (patch.getDeliveryCost() != null) {
            deliveryDTO.setDeliveryCost(patch.getDeliveryCost());
        }

        if (patch.getShipper() != null) {
            deliveryDTO.setShipper(patch.getShipper());
        }

        if (patch.getSenderAddress() != null) {
            deliveryDTO.setSenderAddress(patch.getSenderAddress());
        }

        if (patch.getReceiverAddress() != null) {
            deliveryDTO.setReceiverAddress(patch.getReceiverAddress());
        }

        Delivery delivery = mapToEntity(deliveryDTO);
        delivery = deliveryRepository.save(delivery);
        return mapToDTO(delivery);
    }

    @Override
    public void deleteDelivery(String id) {
        deliveryRepository.findById(Long.valueOf(id)).orElseThrow(EntityNotFoundException::new);
        deliveryRepository.deleteById(Long.valueOf(id));
    }

    @Override
    public DeliveryDTO getDeliveryById(String id) {
        Delivery delivery = deliveryRepository.findById(Long.valueOf(id)).orElseThrow(EntityNotFoundException::new);
        return mapToDTO(delivery);
    }
    public Delivery mapToEntity(DeliveryDTO deliveryDTO) {
        return modelMapper.map(deliveryDTO, Delivery.class);
    }

    public Order mapToEntity(OrderDTO orderDTO) {
        return modelMapper.map(orderDTO, Order.class);
    }

    public DeliveryDTO mapToDTO(Delivery delivery) {
        return modelMapper.map(delivery, DeliveryDTO.class);
    }

    private void throwOnIdMismatch(String id, DeliveryDTO deliveryDTO) {
        if (deliveryDTO.getId() != null && !deliveryDTO.getId().equals(id))
            throw new IdMismatchException();
    }
}
