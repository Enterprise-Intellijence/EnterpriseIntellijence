package com.enterpriseintellijence.enterpriseintellijence.controller;

import com.enterpriseintellijence.enterpriseintellijence.data.services.OrderService;
import com.enterpriseintellijence.enterpriseintellijence.dto.creation.OrderCreateDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.OrderDTO;

import com.enterpriseintellijence.enterpriseintellijence.security.JwtContextUtils;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/orders", produces = "application/json")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class OrderController {
    // TODO: 16/05/23 Erne 
    private final OrderService orderService;
    private final JwtContextUtils jwtContextUtils;

    private final Bandwidth limit = Bandwidth.classic(20, Refill.greedy(25, Duration.ofMinutes(1)));
    private final Bucket bucket = Bucket.builder().addLimit(limit).build();

    @PostMapping(consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<OrderDTO> createOrder(@Valid @RequestBody OrderCreateDTO orderDTO) throws IllegalAccessException {
        return ResponseEntity.ok(orderService.createOrder(orderDTO));
    }

    @GetMapping(path = "/me", params = {"page", "size"}, consumes = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Page<OrderDTO>> getAllOrdersOfUser(@RequestBody Pageable pageable) {
        return ResponseEntity.ok((Page<OrderDTO>) orderService.findAllByUserId(jwtContextUtils.getUserLoggedFromContext().getId(), pageable));
    }

    @PutMapping(path = "/{id}", consumes = "application/json")
    public ResponseEntity<OrderDTO> replaceOrder(@PathVariable("id") String id, @Valid @RequestBody OrderDTO orderDTO) throws IllegalAccessException {
        return ResponseEntity.ok(orderService.replaceOrder(id, orderDTO));
    }

    @PatchMapping(path = "/{id}", consumes = "application/json")
    public ResponseEntity<OrderDTO> updateOrder(@PathVariable("id") String id, @Valid @RequestBody OrderDTO orderDTO) throws IllegalAccessException {
        return ResponseEntity.ok(orderService.updateOrder(id, orderDTO));
    }

    @DeleteMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteOrder(@PathVariable("id") String id) throws IllegalAccessException {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<OrderDTO> getOrder(@PathVariable("id") String id) throws IllegalAccessException {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

}


