package com.enterpriseintellijence.enterpriseintellijence.controller;

import com.enterpriseintellijence.enterpriseintellijence.data.services.OrderService;
import com.enterpriseintellijence.enterpriseintellijence.dto.OrderDTO;
import com.github.fge.jsonpatch.JsonPatch;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/orders", produces = "application/json")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class OrderController {

    private final OrderService orderService;

    @PostMapping(consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public OrderDTO createOrder(@RequestBody OrderDTO orderDTO) {
        return orderService.createOrder(orderDTO);
    }

    @PutMapping(path = "/{id}", consumes = "application/json")
    public ResponseEntity<OrderDTO> replaceOrder(@PathVariable("id") String id, @RequestBody OrderDTO orderDTO) {
        return orderService.replaceOrder(id, orderDTO);
    }

    @PatchMapping(path = "/{id}", consumes = "application/json")
    public ResponseEntity<OrderDTO> updateOrder(@PathVariable("id") String id, @RequestBody JsonPatch jsonPatch) {
        return orderService.updateOrder(id, jsonPatch);
    }

    @DeleteMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<OrderDTO> deleteOrder(@PathVariable("id") String id) {
        return orderService.deleteOrder(id);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<OrderDTO> getOrder(@PathVariable("id") String id) {
        return orderService.getOrderById(id);
    }
}


