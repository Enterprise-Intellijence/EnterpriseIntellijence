package com.enterpriseintellijence.enterpriseintellijence.data.services;

import com.enterpriseintellijence.enterpriseintellijence.core.services.ProcessSaleServiceImp;
import com.enterpriseintellijence.enterpriseintellijence.data.entities.Delivery;
import com.enterpriseintellijence.enterpriseintellijence.data.entities.Order;
import com.enterpriseintellijence.enterpriseintellijence.data.entities.Product;
import com.enterpriseintellijence.enterpriseintellijence.data.entities.User;
import com.enterpriseintellijence.enterpriseintellijence.data.entities.embedded.CustomMoney;
import com.enterpriseintellijence.enterpriseintellijence.data.repository.DeliveryRepository;
import com.enterpriseintellijence.enterpriseintellijence.data.repository.OrderRepository;
import com.enterpriseintellijence.enterpriseintellijence.dto.CustomMoneyDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.DeliveryDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.OrderDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.basics.OrderBasicDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.creation.DeliveryCreateDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.DeliveryStatus;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.OrderState;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.TransactionState;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.UserRole;
import com.enterpriseintellijence.enterpriseintellijence.exception.IdMismatchException;
import com.enterpriseintellijence.enterpriseintellijence.security.JwtContextUtils;
import lombok.RequiredArgsConstructor;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class DeliveryServiceImp implements DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final OrderRepository orderRepository;
    private final JwtContextUtils jwtContextUtils;
    private final ProcessSaleServiceImp processSaleServiceImp;
    private final ModelMapper modelMapper;
    private final Clock clock;

    @Override
    public DeliveryDTO createDelivery(DeliveryCreateDTO deliveryDTO) throws IllegalAccessException {
        // TODO: 29/05/2023 io uso solo id order del deliveryDTO e lo shipper...usiamo parameter?

        Order order = orderRepository.findById(deliveryDTO.getOrder().getId()).orElseThrow(EntityNotFoundException::new);
        Product product = order.getProduct();
        User loggedUser = jwtContextUtils.getUserLoggedFromContext();
        if(loggedUser.getRole().equals(UserRole.USER) && !loggedUser.getId().equals(order.getProduct().getSeller().getId()))
            throw new IllegalAccessException("Only seller can create a delivery");

        if(order.getTransaction()!=null && !!order.getTransaction().getTransactionState().equals(TransactionState.COMPLETED))
            throw new IllegalAccessException("cant create delivery with no transaction completed");

        Delivery delivery = processSaleServiceImp.sendProduct(order,loggedUser,deliveryDTO.getShipper());


        deliveryRepository.save(delivery);
        return mapToDTO(delivery);
    }

    @Override
    public DeliveryDTO replaceDelivery(String id, DeliveryDTO deliveryDTO) throws IllegalAccessException {
        // TODO: 25/05/2023 ha senso di esistere?
        throwOnIdMismatch(id, deliveryDTO);

        Delivery oldDelivery = deliveryRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        Delivery newDelivery = mapToEntity(deliveryDTO);

        if (!oldDelivery.getOrder().getId().equals(newDelivery.getOrder().getId())) {
            throw new IllegalAccessException("Can't change delivery");
        }

        newDelivery = deliveryRepository.save(newDelivery);
        return updateDelivery(id,deliveryDTO);
    }

    @Override
    public DeliveryDTO updateDelivery(String Id, DeliveryDTO patch) throws IllegalAccessException {
        // TODO: 29/05/2023 sistemare
        throwOnIdMismatch(Id, patch);
        Delivery delivery = deliveryRepository.findById(Id).orElseThrow(EntityNotFoundException::new);
        Order order = delivery.getOrder();
        Product product = order.getProduct();
        User loggedUser = jwtContextUtils.getUserLoggedFromContext();

        if(loggedUser.getRole().equals(UserRole.USER) && !loggedUser.getId().equals(order.getProduct().getSeller().getId()))
            throw new IllegalAccessException("Only seller can update a delivery");

        if(patch.getDeliveryStatus().equals(DeliveryStatus.DELIVERED)){
            processSaleServiceImp.productDelivered(order,loggedUser,delivery);
            deliveryRepository.save(delivery);
        }
        else
            throw new IllegalArgumentException("Can only update delivered product, check your parameter and try again");

        return mapToDTO(delivery);
    }

    @Override
    public void deleteDelivery(String id) throws IllegalAccessException {
        Delivery delivery = deliveryRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        Order order = delivery.getOrder();
        User loggedUser = jwtContextUtils.getUserLoggedFromContext();

        if(loggedUser.getRole().equals(UserRole.USER) && !loggedUser.getId().equals(order.getProduct().getSeller().getId()))
            throw new IllegalAccessException("Only seller can update a delivery");

        // TODO: 25/05/2023 perchè si dovrebbe deletare una consegna?
        deliveryRepository.delete(delivery);
    }

    @Override
    public DeliveryDTO getDeliveryById(String id) throws IllegalAccessException {
        Delivery delivery = deliveryRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        Order order = delivery.getOrder();
        User loggedUser = jwtContextUtils.getUserLoggedFromContext();
        if(loggedUser.getRole().equals(UserRole.USER) && (loggedUser.getId().equals(order.getProduct().getSeller().getId()) || loggedUser.getId().equals(order.getUser().getId()) ))
            return mapToDTO(delivery);
        else if(!loggedUser.getRole().equals(UserRole.USER))
            return mapToDTO(delivery);
        else
            throw new IllegalAccessException("Only seller can update a delivery");
    }

    public Delivery mapToEntity(DeliveryDTO deliveryDTO) {
        return modelMapper.map(deliveryDTO, Delivery.class);
    }

    public Order mapToEntity(OrderBasicDTO orderDTO) {
        return modelMapper.map(orderDTO, Order.class);
    }

    public DeliveryDTO mapToDTO(Delivery delivery) {
        return modelMapper.map(delivery, DeliveryDTO.class);
    }

    public CustomMoney mapToEntity(CustomMoneyDTO customMoneyDTO) {
        return modelMapper.map(customMoneyDTO, CustomMoney.class);
    }

    private void throwOnIdMismatch(String id, DeliveryDTO deliveryDTO) {
        if (deliveryDTO.getId() != null && !deliveryDTO.getId().equals(id))
            throw new IdMismatchException();
    }
}
