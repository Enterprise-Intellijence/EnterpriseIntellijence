package com.enterpriseintellijence.enterpriseintellijence.data.services;

import com.enterpriseintellijence.enterpriseintellijence.dto.OrderDTO;
import org.springframework.http.ResponseEntity;

public interface OrderService {

    OrderDTO createOrder(OrderDTO orderDTO);
    ResponseEntity<OrderDTO> replaceOrder(String id, OrderDTO orderDTO);
    ResponseEntity<OrderDTO> updateOrder(String id, OrderDTO orderDTO);
    ResponseEntity<OrderDTO> deleteOrder(String id);
    ResponseEntity<OrderDTO> getOrder(String id);

}
