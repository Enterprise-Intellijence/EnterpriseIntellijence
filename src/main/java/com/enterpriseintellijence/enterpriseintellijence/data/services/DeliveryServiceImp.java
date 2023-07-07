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

    @Transactional
    @Override
    public void deleteDelivery(String id) throws IllegalAccessException {
        Delivery delivery = deliveryRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        Order order = delivery.getOrder();
        User loggedUser = jwtContextUtils.getUserLoggedFromContext();

        if(loggedUser.getRole().equals(UserRole.USER) && !loggedUser.getId().equals(order.getProduct().getSeller().getId()))
            throw new IllegalAccessException("Only seller can update a delivery");

        // TODO: 25/05/2023 perchè si dovrebbe deletare una consegna?
        order.setDelivery(null);
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

    @Override
    public AddressDTO createAddress(AddressCreateDTO addressCreateDTO) {
        User loggedUser = jwtContextUtils.getUserLoggedFromContext();

        Address address = modelMapper.map(addressCreateDTO,Address.class);
        address.setUser(loggedUser);
        if(addressCreateDTO.isDefault()){
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

        if(addressDTO.getHeader()!=null && !addressDTO.getHeader().equals(address.getHeader()))
            address.setHeader(addressDTO.getHeader());
        if(addressDTO.getCountry()!=null && !addressDTO.getCountry().equals(address.getCountry()))
            address.setCountry(addressDTO.getCountry());
        if(addressDTO.getCity()!=null && !addressDTO.getCity().equals(address.getCity()))
            address.setCity(addressDTO.getCity());
        if(addressDTO.getStreet()!=null && !addressDTO.getStreet().equals(address.getStreet()))
            address.setStreet(addressDTO.getStreet());
        if(addressDTO.getZipCode()!=null && !addressDTO.getZipCode().equals(address.getZipCode()))
            address.setZipCode(addressDTO.getZipCode());
        if(addressDTO.getPhoneNumber()!=null && !addressDTO.getPhoneNumber().equals(address.getPhoneNumber()))
            address.setPhoneNumber(addressDTO.getPhoneNumber());
        if(addressDTO.isDefault() && !address.isDefault()){
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
        List<AddressDTO> addressList = loggedUser.getAddresses().stream().map(s->modelMapper.map(s,AddressDTO.class)).collect(Collectors.toList());
        return addressList;
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
