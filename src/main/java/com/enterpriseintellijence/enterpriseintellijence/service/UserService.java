package com.enterpriseintellijence.enterpriseintellijence.service;

import com.enterpriseintellijence.enterpriseintellijence.data.entities.User;
import com.enterpriseintellijence.enterpriseintellijence.data.repository.UserRepository;
import com.enterpriseintellijence.enterpriseintellijence.dto.UserDTO;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private  final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public UserDTO createUser(UserDTO userDTO) {
        User user = modelMapper.map(userDTO,User.class);
        userRepository.save(user);
        // TODO: 21/04/2023
        return null;
    }

    public ResponseEntity<UserDTO> replaceUser(UserDTO userDTO) {
        // TODO: 21/04/2023
        return null;
    }

    public ResponseEntity<UserDTO> updateUser(UserDTO userDTO) {
        // TODO: 21/04/2023
        return null;
    }

    public void deleteUser(String id) {
        // TODO: 21/04/2023

    }

    public ResponseEntity<UserDTO> userById(String id) {
        // TODO: 21/04/2023
        return null;
    }

    public Iterable<UserDTO> findAll() {
        // TODO: 21/04/2023
        return null;
    }
}
