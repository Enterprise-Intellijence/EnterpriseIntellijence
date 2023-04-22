package com.enterpriseintellijence.enterpriseintellijence.data.services;

import com.enterpriseintellijence.enterpriseintellijence.dto.OrderDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class OrderServiceImp implements OrderService{
    @Override
    public OrderDTO createOrder(OrderDTO orderDTO) {
        return null;
    }

    @Override
    public ResponseEntity<OrderDTO> replaceOrder(String id, OrderDTO orderDTO) {
        return null;
    }

    @Override
    public ResponseEntity<OrderDTO> updateOrder(String id, OrderDTO orderDTO) {
        return null;
    }

    @Override
    public ResponseEntity<OrderDTO> deleteOrder(String id) {
        return null;
    }

    @Override
    public ResponseEntity<OrderDTO> getOrder(String id) {
        return null;
    }
}
