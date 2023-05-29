package com.enterpriseintellijence.enterpriseintellijence.data.services;

import com.enterpriseintellijence.enterpriseintellijence.core.services.ProcessSaleServiceImp;
import com.enterpriseintellijence.enterpriseintellijence.data.entities.Order;
import com.enterpriseintellijence.enterpriseintellijence.data.entities.Product;
import com.enterpriseintellijence.enterpriseintellijence.data.entities.User;
import com.enterpriseintellijence.enterpriseintellijence.data.repository.OfferRepository;
import com.enterpriseintellijence.enterpriseintellijence.data.repository.OrderRepository;
import com.enterpriseintellijence.enterpriseintellijence.data.repository.ProductRepository;
import com.enterpriseintellijence.enterpriseintellijence.dto.creation.OrderCreateDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.OrderDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.UserDTO;
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
import java.time.LocalDateTime;

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

    @Override
    public OrderDTO createOrder(OrderCreateDTO orderDTO) throws IllegalAccessException {
        User loggedUser = jwtContextUtils.getUserLoggedFromContext();
        Product product = productRepository.findById(orderDTO.getProduct().getId()).orElseThrow(EntityNotFoundException::new);

        if(!product.getAvailability().equals(Availability.AVAILABLE))
            throw new IllegalArgumentException("cannot buy product not available");

        Order order = processSaleServiceImp.buyProduct(product,loggedUser);



        order = orderRepository.save(order);
        return mapToDTO(order);
    }

    @Override
    public OrderDTO replaceOrder(String id, OrderDTO orderDTO) throws IllegalAccessException {
        // TODO: 25/05/2023 ha senso di esistere?
        throwOnIdMismatch(id, orderDTO);

        Order oldOrder = orderRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        Order newOrder = mapToEntity(orderDTO);

        UserDTO requestingUser = userService.findUserFromContext()
            .orElseThrow(EntityNotFoundException::new);

        if(!requestingUser.getId().equals(oldOrder.getUser().getId())) {
            throw new IllegalAccessException("User cannot change order");
        }

        if(!requestingUser.getId().equals(newOrder.getUser().getId())) {
            throw new IllegalAccessException("User cannot change order");
        }

        if(!oldOrder.getState().equals(newOrder.getState())) {
            throw new IllegalAccessException("State cannot be changed");
        }

        newOrder = orderRepository.save(newOrder);
        return mapToDTO(newOrder);
    }

    @Override
    public OrderDTO updateOrder(String id, OrderDTO patch) throws IllegalAccessException {

        Order order = orderRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        throwOnIdMismatch(id,patch);
        User loggedUser = jwtContextUtils.getUserLoggedFromContext();


        if (loggedUser.getId().equals(order.getUser().getId()) && patch.getState().equals(OrderState.DELIVERED)) {
            processSaleServiceImp.completeOrder(order,loggedUser);
        }
        else
            throw new IllegalAccessException("User cannot change order");


        //per fare una recensione, chi compra, deve fare l'update dell'ordine e settarlo su completato
        orderRepository.save(order);
        return mapToDTO(order);

        /*

        OrderDTO orderDTO = mapToDTO(orderRepository.findById(id).orElseThrow(EntityNotFoundException::new));

        UserDTO userDTO = userService.findUserFromContext()
                .orElseThrow(EntityNotFoundException::new);



        if (Arrays.asList(OrderState.values()).contains(patch.getState())) {

            switch (orderDTO.getState()) {

                case CANCELED -> {
                    if (!patch.getState().equals(OrderState.COMPLETED)) {
                        throw new IllegalAccessException("State cannot be changed");
                    }
                }

                case PENDING -> {
                    if (!(patch.getState().equals(OrderState.CANCELED) ||
                        patch.getState().equals(OrderState.PURCHASED))) {
                        throw new IllegalAccessException("State cannot be changed");
                    }
                }

                case PURCHASED -> {
                    if (patch.getState().equals(OrderState.PENDING) ||
                        patch.getState().equals(OrderState.DELIVERED) ||
                        patch.getState().equals(OrderState.COMPLETED) ||
                        patch.getState().equals(OrderState.CANCELED)) {
                        throw new IllegalAccessException("State cannot be changed");
                    }
                }

                case SHIPPED -> {
                    if (patch.getState().equals(OrderState.PENDING) ||
                        patch.getState().equals(OrderState.PURCHASED) ||
                        patch.getState().equals(OrderState.COMPLETED) ||
                        patch.getState().equals(OrderState.CANCELED)) {
                        throw new IllegalAccessException("State cannot be changed");
                    }
                }

                case DELIVERED -> {
                    if (patch.getState().equals(OrderState.PENDING) ||
                        patch.getState().equals(OrderState.PURCHASED) ||
                        patch.getState().equals(OrderState.SHIPPED) ||
                        patch.getState().equals(OrderState.CANCELED)) {
                        throw new IllegalAccessException("State cannot be changed");
                    }
                }

                case COMPLETED -> {
                    if (!patch.getState().equals(OrderState.CANCELED)) {
                        throw new IllegalAccessException("State cannot be changed");
                    }
                }
            }

            orderDTO.setState(patch.getState());

        } else {
            throw new IllegalAccessException("State cannot be changed");
        }

        orderDTO = mapToDTO(orderRepository.save(mapToEntity(orderDTO)));
        return orderDTO;
*/
    }

    @Override
    public void deleteOrder(String id) throws IllegalAccessException {
        Order order = orderRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        User loggedUser = jwtContextUtils.getUserLoggedFromContext();

        if (loggedUser.getId().equals(order.getUser().getId()) && order.getState().equals(OrderState.PENDING)) {
            processSaleServiceImp.cancelOrder(order,loggedUser);
        }
        else if(loggedUser.getId().equals(order.getProduct().getSeller().getId()) || order.getState().equals(OrderState.CANCELED)){
            Product product = order.getProduct();
            product.setAvailability(Availability.AVAILABLE);
            product.setOrder(null);
            productRepository.save(product);
            orderRepository.delete(order);
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

    @Override
    public Iterable<OrderDTO> findAllByUserId(String userId, Pageable pageable) {
        // TODO: 22/05/2023  
        return null;
    }

    public Page<OrderDTO> findAllByUserId(Pageable pageable) {

        UserDTO userDTO = userService.findUserFromContext()
            .orElseThrow(EntityNotFoundException::new);

        return orderRepository.findAllByUserId(userDTO.getId(), pageable).map(this::mapToDTO);
    }

    private void throwOnIdMismatch(String id, OrderDTO orderDTO) {
        if (orderDTO.getId() != null && !orderDTO.getId().equals(id))
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
}
