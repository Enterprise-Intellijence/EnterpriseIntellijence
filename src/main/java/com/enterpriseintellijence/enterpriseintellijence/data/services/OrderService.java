package com.enterpriseintellijence.enterpriseintellijence.data.services;

import com.enterpriseintellijence.enterpriseintellijence.dto.OrderDTO;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;

public interface OrderService {

    OrderDTO createOrder(OrderDTO orderDTO);
    ResponseEntity<OrderDTO> replaceOrder(OrderDTO orderDTO);
    ResponseEntity<OrderDTO> updateOrder(OrderDTO orderDTO);
    ResponseEntity<OrderDTO> deleteOrder(String id);
    ResponseEntity<OrderDTO> getOrder(String id);

}
