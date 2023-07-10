package com.enterpriseintellijence.enterpriseintellijence.data.services;

import com.enterpriseintellijence.enterpriseintellijence.core.services.ProcessSaleServiceImp;
import com.enterpriseintellijence.enterpriseintellijence.data.entities.*;
import com.enterpriseintellijence.enterpriseintellijence.data.entities.embedded.CustomMoney;
import com.enterpriseintellijence.enterpriseintellijence.data.repository.AddressRepository;
import com.enterpriseintellijence.enterpriseintellijence.data.repository.DeliveryRepository;
import com.enterpriseintellijence.enterpriseintellijence.data.repository.OrderRepository;
import com.enterpriseintellijence.enterpriseintellijence.dto.AddressDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.CustomMoneyDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.DeliveryDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.OrderDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.basics.OrderBasicDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.creation.AddressCreateDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.creation.DeliveryCreateDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.DeliveryStatus;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.OrderState;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.TransactionState;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.UserRole;
import com.enterpriseintellijence.enterpriseintellijence.exception.IdMismatchException;
import com.enterpriseintellijence.enterpriseintellijence.security.JwtContextUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DeliveryServiceImp implements DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final OrderRepository orderRepository;
    private final JwtContextUtils jwtContextUtils;
    private final ProcessSaleServiceImp processSaleServiceImp;
    private final ModelMapper modelMapper;
    private final Clock clock;
    private final AddressRepository addressRepository;

    @Override
    public DeliveryDTO createDelivery(DeliveryCreateDTO deliveryDTO) throws IllegalAccessException {

        Order order = orderRepository.findById(deliveryDTO.getOrder().getId()).orElseThrow(EntityNotFoundException::new);

        User loggedUser = jwtContextUtils.getUserLoggedFromContext();
        if(loggedUser.getRole().equals(UserRole.USER) && !loggedUser.getId().equals(order.getProduct().getSeller().getId()))
            throw new IllegalAccessException("Only seller can create a delivery");

        if(order.getTransaction()!=null && !order.getTransaction().getTransactionState().equals(TransactionState.COMPLETED))
            throw new IllegalAccessException("cant create delivery with no transaction completed");

        if(order.getTransaction() == null)
            throw new IllegalAccessException("cant create delivery with no transaction");

        return mapToDTO(processSaleServiceImp.sendProduct(order,loggedUser,deliveryDTO.getShipper()));
    }

    /*
    @Override
    public DeliveryDTO replaceDelivery(String id, DeliveryDTO deliveryDTO) throws IllegalAccessException {
        // TODO: 25/05/2023 ha senso di esistere? Effettivamente non si dovrebbe poter cambiare una consegna
        throwOnIdMismatch(id, deliveryDTO);

        Delivery oldDelivery = deliveryRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        Delivery newDelivery = mapToEntity(deliveryDTO);

        if (!oldDelivery.getOrder().getId().equals(newDelivery.getOrder().getId())) {
            throw new IllegalAccessException("Can't change delivery");
        }

        newDelivery = deliveryRepository.save(newDelivery);
        return updateDelivery(id,deliveryDTO);
    }

     */

    @Override
    public DeliveryDTO updateDelivery(String Id, DeliveryDTO patch) throws IllegalAccessException {

        throwOnIdMismatch(Id, patch);
        Delivery delivery = deliveryRepository.findById(Id).orElseThrow(EntityNotFoundException::new);
        Order order = delivery.getOrder();

        User loggedUser = jwtContextUtils.getUserLoggedFromContext();

        if(loggedUser.getRole().equals(UserRole.USER) && !loggedUser.getId().equals(order.getProduct().getSeller().getId()))
            throw new IllegalAccessException("Only seller can update a delivery");

        if(patch.getDeliveryStatus().equals(DeliveryStatus.DELIVERED)){
            processSaleServiceImp.productDelivered(order,loggedUser,delivery);
        }
        else
            throw new IllegalArgumentException("Can only update delivered product, check your parameter and try again");

        return mapToDTO(delivery);
    }


    /*
    @Transactional
    @Override
    public void deleteDelivery(String id) throws IllegalAccessException {
        Delivery delivery = deliveryRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        Order order = delivery.getOrder();
        User loggedUser = jwtContextUtils.getUserLoggedFromContext();

        if(loggedUser.getRole().equals(UserRole.USER) && !loggedUser.getId().equals(order.getProduct().getSeller().getId()))
            throw new IllegalAccessException("Only seller can update a delivery");

        // TODO: 25/05/2023 perchè si dovrebbe deletare una consegna?   infatti non ha senso
        order.setDelivery(null);
        deliveryRepository.delete(delivery);
    }

     */

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

    @Override
    public AddressDTO createAddress(AddressCreateDTO addressCreateDTO) {
        User loggedUser = jwtContextUtils.getUserLoggedFromContext();

        Address address = modelMapper.map(addressCreateDTO,Address.class);
        address.setUser(loggedUser);
        if(addressCreateDTO.getIsDefault()){
            for (Address address1: loggedUser.getAddresses()){
                if(address1.isDefault()){
                    address1.setDefault(false);
                    addressRepository.save(address1);
                }
            }
        }
        addressRepository.save(address);
        return modelMapper.map(address,AddressDTO.class);
    }

    @Override
    public AddressDTO replaceAddress(String id, AddressDTO addressDTO) throws IllegalAccessException {
        return updateAddress(id,addressDTO);
    }

    @Override
    public AddressDTO updateAddress(String id, AddressDTO addressDTO) throws IllegalAccessException {
        if(addressDTO!=null && !id.equals(addressDTO.getId()))
            throw new IdMismatchException();
        Address address = addressRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        User loggedUser = jwtContextUtils.getUserLoggedFromContext();
        if(!address.getUser().equals(loggedUser))
            throw new IllegalAccessException("Cannot modify address of other user");

        assert addressDTO != null;
        address.setHeader(addressDTO.getHeader());
        address.setCountry(addressDTO.getCountry());
        address.setCity(addressDTO.getCity());
        address.setStreet(addressDTO.getStreet());
        address.setZipCode(addressDTO.getZipCode());
        address.setPhoneNumber(addressDTO.getPhoneNumber());
        if(addressDTO.getIsDefault() && !address.isDefault()){
            for(Address address1: loggedUser.getAddresses()){
                if(address1.isDefault() && !address1.equals(address)){
                    address1.setDefault(false);
                    addressRepository.save(address1);
                }
            }
            address.setDefault(true);
        }


        addressRepository.save(address);


        return modelMapper.map(address,AddressDTO.class);
    }

    @Override
    public void deleteAddress(String id) throws IllegalAccessException {
        Address address = addressRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        User loggedUser = jwtContextUtils.getUserLoggedFromContext();
        if(loggedUser.getRole().equals(UserRole.USER) && !address.getUser().equals(loggedUser))
            throw new IllegalAccessException("Cannot delete address of other user");
        if (address.getOrders() != null && !address.getOrders().isEmpty())
            throw new IllegalArgumentException("Cannot delete address with orders");

        if (address.isDefault()){
            for (Address address1: loggedUser.getAddresses()){
                if(!address1.equals(address)){
                    address1.setDefault(true);
                    addressRepository.save(address1);
                    break;
                }
            }
        }
        addressRepository.delete(address);
    }

    @Override
    public AddressDTO getAddress(String id) {
        Address address = addressRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        return modelMapper.map(address,AddressDTO.class);
    }

    @Override
    public Iterable<AddressDTO> getMyAddressList() throws IllegalAccessException {
        User loggedUser = jwtContextUtils.getUserLoggedFromContext();
        return loggedUser.getAddresses().stream().map(s->modelMapper.map(s,AddressDTO.class)).collect(Collectors.toList());
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
        if (!deliveryDTO.getId().equals(id))
            throw new IdMismatchException();
    }
}
