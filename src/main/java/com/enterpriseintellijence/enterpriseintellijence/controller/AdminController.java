package com.enterpriseintellijence.enterpriseintellijence.controller;

import com.enterpriseintellijence.enterpriseintellijence.data.services.ProductService;
import com.enterpriseintellijence.enterpriseintellijence.data.services.UserService;
import com.enterpriseintellijence.enterpriseintellijence.dto.ProductDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.UserDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.UserRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(path="/api/v1/admin", produces="application/json")
@CrossOrigin(origins="*")
@Slf4j
public class AdminController {

    private final UserService userService;
    private final ProductService productService;

    @GetMapping("/users")
    public ResponseEntity<Page<UserDTO>> getUsers(@RequestParam int page, @RequestParam int size) {
        return ResponseEntity.ok(userService.findAll(page, size));
    }

    @PostMapping("/users/ban/{userId}")
    public ResponseEntity<UserDTO> banUser(@PathVariable String userId) {
        return ResponseEntity.ok(userService.banUser(userId));
    }

    @PostMapping("/users/unban/{userId}")
    public ResponseEntity<UserDTO> unbanUser(@PathVariable String userId) {
        return ResponseEntity.ok(userService.unBanUser(userId));
    }

    @GetMapping("/products/{productId}")
    public ResponseEntity<ProductDTO> getProduct(@PathVariable String productId) {
        return ResponseEntity.ok(productService.getProductById(productId, true));
    }

    @DeleteMapping("/products/{productId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable String productId) throws IllegalAccessException {
        productService.deleteProduct(productId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/products/")
    public ResponseEntity<ProductDTO> updateProduct(@RequestBody ProductDTO productDTO) {
        return ResponseEntity.ok(productService.replaceProduct(productDTO.getId(), productDTO));
    }

}
