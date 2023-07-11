package com.enterpriseintellijence.enterpriseintellijence.controller;

import com.enterpriseintellijence.enterpriseintellijence.data.services.OrderService;
import com.enterpriseintellijence.enterpriseintellijence.dto.basics.OrderBasicDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.creation.OrderCreateDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.OrderDTO;

import com.enterpriseintellijence.enterpriseintellijence.dto.enums.OrderState;
import com.enterpriseintellijence.enterpriseintellijence.security.JwtContextUtils;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.enterpriseintellijence.enterpriseintellijence.security.AppSecurityConfig.SECURITY_CONFIG_NAME;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/orders", produces = "application/json")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@SecurityRequirement(name = SECURITY_CONFIG_NAME)
public class OrderController {
    // TODO: 16/05/23 Erne 
    private final OrderService orderService;
    private final JwtContextUtils jwtContextUtils;


    @PostMapping(consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<OrderDTO> createOrder(@Valid @RequestBody OrderCreateDTO orderDTO) throws IllegalAccessException {
        return ResponseEntity.ok(orderService.createOrder(orderDTO));
    }

    @GetMapping(path = "/me")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Page<OrderBasicDTO>> getAllOrdersOfUser(
        @RequestParam(required = false) OrderState state,
        @RequestParam(defaultValue = "0", required = false) int page,
        @RequestParam(defaultValue = "10", required = false) int sizePage) {
        return ResponseEntity.ok(orderService.findAllMyOrdersByState(PageRequest.of(page, sizePage), state));
    }

    /*
    @PutMapping(path = "/{id}", consumes = "application/json")
    public ResponseEntity<OrderDTO> replaceOrder(@PathVariable("id") String id, @Valid @RequestBody OrderDTO orderDTO) throws IllegalAccessException {
        return ResponseEntity.ok(orderService.replaceOrder(id, orderDTO));
    }

     */

    @PutMapping(path = "/{id}", consumes = "application/json")
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


