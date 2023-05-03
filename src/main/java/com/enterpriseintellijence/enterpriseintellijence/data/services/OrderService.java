package com.enterpriseintellijence.enterpriseintellijence.data.services;

import com.enterpriseintellijence.enterpriseintellijence.dto.OrderCreateDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.OrderDTO;

import org.springframework.data.domain.Pageable;

public interface OrderService {

    OrderDTO createOrder(OrderCreateDTO orderDTO);
    OrderDTO replaceOrder(String id, OrderDTO orderDTO) throws IllegalAccessException;
    OrderDTO updateOrder(String id, OrderDTO orderDTO) throws IllegalAccessException;
    void deleteOrder(String id) throws IllegalAccessException;
    OrderDTO getOrderById(String id) throws IllegalAccessException;
    public Iterable<OrderDTO> findAllByUserId(Pageable pageable);

}
