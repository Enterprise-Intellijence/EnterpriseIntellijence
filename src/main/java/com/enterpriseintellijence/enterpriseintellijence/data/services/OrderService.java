package com.enterpriseintellijence.enterpriseintellijence.data.services;

import com.enterpriseintellijence.enterpriseintellijence.dto.basics.OrderBasicDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.creation.OrderCreateDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.OrderDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {

    OrderDTO createOrder(OrderCreateDTO orderDTO) throws IllegalAccessException;
    OrderDTO updateOrder(String id, OrderDTO orderDTO) throws IllegalAccessException;
    void deleteOrder(String id) throws IllegalAccessException;
    OrderDTO getOrderById(String id) throws IllegalAccessException;
    public Page<OrderBasicDTO> findAllByUserId(Pageable pageable);

}
