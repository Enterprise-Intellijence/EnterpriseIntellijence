package com.enterpriseintellijence.enterpriseintellijence.data.services;

import com.enterpriseintellijence.enterpriseintellijence.dto.OrderCreateDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.OrderDTO;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;

import org.springframework.http.ResponseEntity;

public interface OrderService {

    OrderDTO createOrder(OrderCreateDTO orderDTO);
    OrderDTO replaceOrder(String id, OrderDTO orderDTO) throws IllegalAccessException;
    OrderDTO updateOrder(String id, JsonPatch orderDTO) throws JsonPatchException;
    OrderDTO deleteOrder(String id);
    OrderDTO getOrderById(String id) throws IllegalAccessException;

}
