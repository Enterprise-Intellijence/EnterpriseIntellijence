package com.enterpriseintellijence.enterpriseintellijence.controller;

import com.enterpriseintellijence.enterpriseintellijence.dto.ProductDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.UserDTO;
import com.enterpriseintellijence.enterpriseintellijence.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(path="/api/v1/users", produces="application/json")
public class UserController {
    private final UserService userService;

    @PostMapping(consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDTO createUser(@RequestBody UserDTO userDTO){
        return userService.createUser(userDTO);
    }

    @PutMapping(path = "/{id}",consumes="application/json")
    public ResponseEntity<UserDTO> replaceUser(@PathVariable("id") String id, @RequestBody UserDTO userDTO){
        return userService.replaceUser(userDTO);
    }

    @PatchMapping(path="/{id}", consumes = "application/json")
    public ResponseEntity<UserDTO> updateUser(@PathVariable("id") String id, @RequestBody UserDTO userDTO) {
        return userService.updateUser(userDTO);
    }

    @DeleteMapping(path="/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable("id") String id){
        userService.deleteUser(id);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> userById(@PathVariable("id") String id){
        return userService.userById(id);
    }

    @GetMapping("")
    public Iterable<UserDTO> allUser() {
        return userService.findAll();
    }

}