package com.enterpriseintellijence.enterpriseintellijence.controller;

import com.enterpriseintellijence.enterpriseintellijence.core.services.AdministrationService;
import com.enterpriseintellijence.enterpriseintellijence.data.services.UserService;
import com.enterpriseintellijence.enterpriseintellijence.dto.ProductCategoryDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.SizeDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.UserDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.UserRole;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static com.enterpriseintellijence.enterpriseintellijence.security.AppSecurityConfig.SECURITY_CONFIG_NAME;

@RestController
@RequiredArgsConstructor
@RequestMapping(path="/api/v1/superAdmin", produces="application/json")
@CrossOrigin(origins="*")
@Slf4j
@SecurityRequirement(name = SECURITY_CONFIG_NAME)
public class SuperAdminController {

    private final UserService userService;
    private final AdministrationService administrationService;

    @PostMapping("/users/changeRole")
    public ResponseEntity<UserDTO> changeRole(@RequestParam String userId, @RequestParam UserRole role) {
        return ResponseEntity.ok(userService.changeRole(userId, role));
    }

    @PostMapping("/categories")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ProductCategoryDTO> createNewCategory(@RequestBody ProductCategoryDTO productCategoryDTO) throws IllegalAccessException {
        return ResponseEntity.ok(administrationService.createNewCategory(productCategoryDTO));
    }

    @PostMapping(value = "/categories/{catId}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void productCategoryEnableOrDisable(@PathVariable("catId") String catId) throws IllegalAccessException {
        administrationService.productCategoryEnableOrDisable(catId);
    }

    @PutMapping(value = "/categories/{catId}",consumes = "application/json")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    public ResponseEntity<ProductCategoryDTO> replaceProductCategory(@PathVariable("catId") String catId,@RequestBody ProductCategoryDTO productCategoryDTO) throws IllegalAccessException {
        return ResponseEntity.ok(administrationService.replaceProductCategory(catId,productCategoryDTO));
    }

    @DeleteMapping(path = "/categories/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public void deleteCategory(@PathVariable("catId") String catId) throws IllegalAccessException {
        administrationService.deleteCategory(catId);
    }

    @PostMapping("/size")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<SizeDTO> createNewSize(@RequestBody SizeDTO sizeDTO) throws IllegalAccessException {
        return ResponseEntity.ok(administrationService.createNewSize(sizeDTO));
    }

    @PutMapping("/size/{sizeId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<SizeDTO> replaceSize(@PathVariable("sizeId") String sizeId,@RequestBody SizeDTO sizeDTO) throws IllegalAccessException {
        return ResponseEntity.ok(administrationService.replaceSize(sizeId,sizeDTO));
    }

    @DeleteMapping(path = "/size/{sizeId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public void deleteSize(
            @PathVariable("sizeId") String sizeId) throws IllegalAccessException {
        administrationService.deleteSize(sizeId);
    }


}
