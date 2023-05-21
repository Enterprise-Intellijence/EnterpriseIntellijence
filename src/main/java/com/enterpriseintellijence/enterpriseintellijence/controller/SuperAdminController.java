package com.enterpriseintellijence.enterpriseintellijence.controller;

import com.enterpriseintellijence.enterpriseintellijence.data.services.UserService;
import com.enterpriseintellijence.enterpriseintellijence.dto.UserDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.UserRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(path="/api/v1/superAdmin", produces="application/json")
@CrossOrigin(origins="*")
@Slf4j
public class SuperAdminController {

    private final UserService userService;

    @PostMapping("/users/changeRole")
    public ResponseEntity<UserDTO> changeRole(@RequestParam String userId, @RequestParam UserRole role) {
        return ResponseEntity.ok(userService.changeRole(userId, role));
    }


}
