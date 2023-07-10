package com.enterpriseintellijence.enterpriseintellijence.controller;

import com.enterpriseintellijence.enterpriseintellijence.data.services.ProductService;
import com.enterpriseintellijence.enterpriseintellijence.data.services.UserService;
import com.enterpriseintellijence.enterpriseintellijence.dto.ProductDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.UserDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.UserRole;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.enterpriseintellijence.enterpriseintellijence.security.AppSecurityConfig.SECURITY_CONFIG_NAME;

@RestController
@RequiredArgsConstructor
@RequestMapping(path="/api/v1/admin", produces="application/json")
@CrossOrigin(origins="*")
@Slf4j
@SecurityRequirement(name = SECURITY_CONFIG_NAME)
public class AdminController {

    private final UserService userService;
    private final ProductService productService;

    @GetMapping("/users")
    public ResponseEntity<Page<UserDTO>> getUsers(@RequestParam int page, @RequestParam int size, @RequestParam UserRole userRole,@RequestParam(required = false) String username) throws IllegalAccessException {
        return ResponseEntity.ok(userService.findAll(page, size,userRole,username));
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

    @PutMapping("/products/")
    public ResponseEntity<ProductDTO> updateProduct(@RequestBody ProductDTO productDTO) throws IllegalAccessException {
        return ResponseEntity.ok(productService.updateProduct(productDTO.getId(), productDTO));
    }

}
