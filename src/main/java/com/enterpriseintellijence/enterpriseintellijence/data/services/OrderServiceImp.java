package com.enterpriseintellijence.enterpriseintellijence.data.services;

import com.enterpriseintellijence.enterpriseintellijence.data.entities.Order;
import com.enterpriseintellijence.enterpriseintellijence.data.repository.OrderRepository;
import com.enterpriseintellijence.enterpriseintellijence.dto.OrderDTO;
import com.enterpriseintellijence.enterpriseintellijence.exception.IdMismatchException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OrderServiceImp implements OrderService{
    
    private final OrderRepository orderRepository;
    private final ModelMapper modelMapper;

    @Override
    public OrderDTO createOrder(OrderDTO orderDTO) {

        orderDTO.setOrderDate(LocalDateTime.now());
        Order order = mapToEntity(orderDTO);
        order = orderRepository.save(order);
        return mapToDTO(order);
    }

    @Override
    public ResponseEntity<OrderDTO> replaceOrder(String id, OrderDTO orderDTO) {
        throwOnIdMismatch(id, orderDTO);
        Order order = mapToEntity(orderDTO);
        order = orderRepository.save(order);
        return new ResponseEntity<>(mapToDTO(order), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<OrderDTO> updateOrder(String id, JsonPatch patch) {
        OrderDTO order = mapToDTO(orderRepository.findById(id).orElseThrow(EntityNotFoundException::new));

        try {
            order = applyPatch(patch, mapToEntity(order));
            orderRepository.save(mapToEntity(order));

        } catch (JsonPatchException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        return new ResponseEntity<>(order, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<OrderDTO> deleteOrder(String id) {
        Order order = orderRepository.findById(id).orElseThrow(EntityNotFoundException::new);

        orderRepository.deleteById(id);
        return new ResponseEntity<>(mapToDTO(order), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<OrderDTO> getOrderById(String id) {
        Order order = orderRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        return new ResponseEntity<>(mapToDTO(order), HttpStatus.OK);
    }

    private void throwOnIdMismatch(String id, OrderDTO orderDTO) {
        if (orderDTO.getId() != null && !orderDTO.getId().equals(id))
            throw new IdMismatchException();
    }

    public OrderDTO applyPatch(JsonPatch patch, Order order) throws JsonPatchException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode patched = patch.apply(objectMapper.convertValue(order, JsonNode.class));

        return objectMapper.convertValue(patched, OrderDTO.class);
    }

    public Order mapToEntity(OrderDTO orderDTO) {
        return modelMapper.map(orderDTO, Order.class);
    }

    public OrderDTO mapToDTO(Order order) {
        return modelMapper.map(order, OrderDTO.class);
    }

}
