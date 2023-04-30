package com.enterpriseintellijence.enterpriseintellijence.data.services;

import com.enterpriseintellijence.enterpriseintellijence.data.entities.Order;
import com.enterpriseintellijence.enterpriseintellijence.data.entities.User;
import com.enterpriseintellijence.enterpriseintellijence.data.repository.OrderRepository;
import com.enterpriseintellijence.enterpriseintellijence.dto.OrderCreateDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.OrderDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.ProductDTO;
import com.enterpriseintellijence.enterpriseintellijence.exception.IdMismatchException;
import com.enterpriseintellijence.enterpriseintellijence.security.JwtContextUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImp implements OrderService {

    private final OrderRepository orderRepository;
    private final UserService userService;
    private final ModelMapper modelMapper;
    private final Clock clock;
    private final ObjectMapper objectMapper;

    @Override
    public OrderDTO createOrder(OrderCreateDTO orderDTO) {
        Order order = mapToEntity(orderDTO);
        order.setOrderDate(LocalDateTime.now(clock));

        UserDTO userDTO = userService.findUserFromContext()
            .orElseThrow(EntityNotFoundException::new);

        order.setUser(modelMapper.map(userDTO, User.class));
        order = orderRepository.save(order);
        return mapToDTO(order);
    }

    @Override
    public OrderDTO replaceOrder(String id, OrderDTO orderDTO) throws IllegalAccessException {
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
    public OrderDTO updateOrder(String id, JsonPatch patch) throws JsonPatchException, IllegalAccessException {

        var mappedPatch = objectMapper.convertValue(patch, JsonNode.class);

        if (!mappedPatch.get("op").toString().equals("replace")) {
            throw new IllegalAccessException("Only replace operation is allowed");
        }

        OrderDTO order = mapToDTO(orderRepository.findById(id).orElseThrow(EntityNotFoundException::new));

        User requestingUser = new User(); //todo: get user from context
        if (!requestingUser.getId().equals(order.getUser().getId())) {
            throw new IllegalAccessException("User cannot change order");
        }

        order = applyPatch(patch, mapToEntity(order));
        orderRepository.save(mapToEntity(order));
        return order;
    }

    @Override
    public OrderDTO deleteOrder(String id) throws IllegalAccessException {
        Order order = orderRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        User user = new User(); //todo: get user from context

        if (!user.getId().equals(order.getUser().getId())) {
            throw new IllegalAccessException("User cannot delete order");
        }

        orderRepository.deleteById(id);
        return mapToDTO(order);
    }

    @Override
    public OrderDTO getOrderById(String id) throws IllegalAccessException {
        Order order = orderRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        OrderDTO orderDTO = mapToDTO(order);
        UserDTO userDTO = userService.findUserFromContext()
            .orElseThrow(EntityNotFoundException::new);
        if(!userDTO.getId().equals(orderDTO.getUser().getId())) {
            throw new IllegalAccessException("User cannot read other's orders");
        }
        return orderDTO;
    }

    public Page<OrderDTO> findAllByUserId(Pageable pageable) {

        User user = new User(); //todo: get user from context
        return orderRepository.findAllByUserId(user.getId(), pageable).map(this::mapToDTO);
    }

    private void throwOnIdMismatch(String id, OrderDTO orderDTO) {
        if (orderDTO.getId() != null && !orderDTO.getId().equals(id))
            throw new IdMismatchException();
    }

    public OrderDTO applyPatch(JsonPatch patch, Order order) throws JsonPatchException {
        JsonNode patched = patch.apply(objectMapper.convertValue(order, JsonNode.class));
        return objectMapper.convertValue(patched, OrderDTO.class);
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
