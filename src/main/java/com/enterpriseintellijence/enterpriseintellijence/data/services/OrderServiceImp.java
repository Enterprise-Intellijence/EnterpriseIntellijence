package com.enterpriseintellijence.enterpriseintellijence.data.services;

import com.enterpriseintellijence.enterpriseintellijence.core.services.ProcessSaleServiceImp;
import com.enterpriseintellijence.enterpriseintellijence.data.entities.Address;
import com.enterpriseintellijence.enterpriseintellijence.data.entities.Order;
import com.enterpriseintellijence.enterpriseintellijence.data.entities.Product;
import com.enterpriseintellijence.enterpriseintellijence.data.entities.User;
import com.enterpriseintellijence.enterpriseintellijence.data.repository.AddressRepository;
import com.enterpriseintellijence.enterpriseintellijence.data.repository.OfferRepository;
import com.enterpriseintellijence.enterpriseintellijence.data.repository.OrderRepository;
import com.enterpriseintellijence.enterpriseintellijence.data.repository.ProductRepository;
import com.enterpriseintellijence.enterpriseintellijence.dto.basics.OrderBasicDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.creation.OrderCreateDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.OrderDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.Availability;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.OrderState;
import com.enterpriseintellijence.enterpriseintellijence.exception.IdMismatchException;

import com.enterpriseintellijence.enterpriseintellijence.security.JwtContextUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Clock;

@Service
@RequiredArgsConstructor
public class OrderServiceImp implements OrderService {

    private final OrderRepository orderRepository;
    private final UserService userService;
    private final JwtContextUtils jwtContextUtils;
    private final ProductRepository productRepository;
    private final OfferRepository offerRepository;
    private final ModelMapper modelMapper;
    private final Clock clock;
    private final ProcessSaleServiceImp processSaleServiceImp;
    private final AddressRepository addressRepository;

    @Override
    public OrderDTO createOrder(OrderCreateDTO orderDTO) throws IllegalAccessException {
        User loggedUser = jwtContextUtils.getUserLoggedFromContext();
        Product product = productRepository.findById(orderDTO.getProduct().getId()).orElseThrow(EntityNotFoundException::new);
        Address address = addressRepository.findById(orderDTO.getDeliveryAddress().getId()).orElseThrow(EntityNotFoundException::new);

        if(!product.getAvailability().equals(Availability.AVAILABLE))
            throw new IllegalArgumentException("cannot buy product not available");

        if(!address.getUser().equals(loggedUser))
            throw new IllegalAccessException("Cannot use address of other user");

        Order order = processSaleServiceImp.buyProduct(product,loggedUser,address);



        order = orderRepository.save(order);
        return mapToDTO(order);
    }

    @Override
    public OrderDTO updateOrder(String id, OrderDTO patch) throws IllegalAccessException {

        Order order = orderRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        throwOnIdMismatch(id,patch);
        User loggedUser = jwtContextUtils.getUserLoggedFromContext();

        // TODO: 05/06/2023
        if (loggedUser.getId().equals(order.getUser().getId()) && order.getState().equals(OrderState.PENDING) && patch.getState().equals(OrderState.CANCELED)) {
            processSaleServiceImp.cancelOrder(order,loggedUser);
        }
        else if (loggedUser.getId().equals(order.getUser().getId()) && order.getState().equals(OrderState.DELIVERED) && patch.getState().equals(OrderState.COMPLETED)) {
            processSaleServiceImp.completeOrder(order,loggedUser);
        }
        else if(patch.getDeliveryAddress()!=null && !patch.getDeliveryAddress().getId().equals(order.getDeliveryAddress().getId())){
            Address address = addressRepository.findById(patch.getDeliveryAddress().getId()).orElseThrow(EntityNotFoundException::new);
            order.setDeliveryAddress(address);
        } 
        else
            throw new IllegalAccessException("User cannot change order");


        //per fare una recensione, chi compra, deve fare l'update dell'ordine e settarlo su completato
        orderRepository.save(order);
        return mapToDTO(order);


    }

    @Override
    public void deleteOrder(String id) throws IllegalAccessException {
        Order order = orderRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        User loggedUser = jwtContextUtils.getUserLoggedFromContext();

        if (loggedUser.getId().equals(order.getUser().getId()) && order.getState().equals(OrderState.PENDING)) {
            processSaleServiceImp.cancelOrder(order,loggedUser);
            orderRepository.save(order);
        }
        else
            throw new IllegalAccessException("User cannot delete order");

    }

    @Override
    public OrderDTO getOrderById(String id) throws IllegalAccessException {
        Order order = orderRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        User loggedUser = jwtContextUtils.getUserLoggedFromContext();
        if(loggedUser.getId().equals(order.getUser().getId()) || loggedUser.getId().equals(order.getProduct().getSeller().getId())) {
            return mapToDTO(order);
        }
        else
            throw new IllegalAccessException("User cannot read other's orders");
    }


    public Page<OrderBasicDTO> findAllMyOrdersByState(Pageable pageable, OrderState state) {
        User user = jwtContextUtils.getUserLoggedFromContext();

        if(state == null)
            return orderRepository.findAllByUser(user, pageable).map(this::mapToBasicDTO);
        else
            return orderRepository.findAllByUserAndState(user, state, pageable).map(this::mapToBasicDTO);

    }

    @Override
    public Page<OrderBasicDTO> findAllMySellerOrdersByState(Pageable pageable, OrderState state) {
        User user = jwtContextUtils.getUserLoggedFromContext();

        if(state == null)
            return orderRepository.findAllByProduct_Seller(user, pageable).map(this::mapToBasicDTO);
        else
            return orderRepository.findAllByProduct_SellerAndState(user, state, pageable).map(this::mapToBasicDTO);

    }



    private void throwOnIdMismatch(String id, OrderDTO orderDTO) {
        if (!orderDTO.getId().equals(id))
            throw new IdMismatchException();
    }

    public Order mapToEntity(OrderDTO orderDTO) {
        return modelMapper.map(orderDTO, Order.class);
    }

    public Order mapToEntity(OrderCreateDTO orderDTO) {
        return modelMapper.map(orderDTO, Order.class);
    }

    public OrderDTO mapToDTO(Order order) {
        return modelMapper.map(order, OrderDTO.class);
    }

    public OrderBasicDTO mapToBasicDTO(Order order){
        return modelMapper.map(order,OrderBasicDTO.class);
    }
}
