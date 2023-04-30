package com.enterpriseintellijence.enterpriseintellijence.controller;

import com.enterpriseintellijence.enterpriseintellijence.data.services.OrderService;
import com.enterpriseintellijence.enterpriseintellijence.dto.OrderCreateDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.OrderDTO;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    public ResponseEntity<OrderDTO> createOrder(@RequestBody OrderCreateDTO orderDTO) {
        return ResponseEntity.ok(orderService.createOrder(orderDTO));
    }

    @GetMapping(path = "/me", params = {"page", "size"}, consumes = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Page<OrderDTO>> getAllOrdersOfUser(@RequestBody Pageable pageable) {
        return ResponseEntity.ok((Page<OrderDTO>) orderService.findAllByUserId(pageable));
    }

    @PutMapping(path = "/{id}", consumes = "application/json")
    public ResponseEntity<OrderDTO> replaceOrder(@PathVariable("id") String id, @RequestBody OrderDTO orderDTO) throws IllegalAccessException {
        return ResponseEntity.ok(orderService.replaceOrder(id, orderDTO));
    }

    @PatchMapping(path = "/{id}", consumes = "application/json")
    public ResponseEntity<OrderDTO> updateOrder(@PathVariable("id") String id, @RequestBody JsonPatch jsonPatch) throws JsonPatchException, IllegalAccessException {
        return ResponseEntity.ok(orderService.updateOrder(id, jsonPatch));
    }

    @DeleteMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<OrderDTO> deleteOrder(@PathVariable("id") String id) throws IllegalAccessException {
        return ResponseEntity.ok(orderService.deleteOrder(id));
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<OrderDTO> getOrder(@PathVariable("id") String id) throws IllegalAccessException {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }
}


