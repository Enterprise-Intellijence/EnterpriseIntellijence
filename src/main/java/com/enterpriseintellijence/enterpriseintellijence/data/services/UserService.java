package com.enterpriseintellijence.enterpriseintellijence.data.services;

import com.enterpriseintellijence.enterpriseintellijence.dto.PaymentMethodDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.UserDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface UserService {
    UserDTO createUser(UserDTO userDTO);
    UserDTO replaceUser(String id, UserDTO userDTO) throws IllegalAccessException;
    UserDTO updateUser(String id, UserDTO patch) throws IllegalAccessException;
    void deleteUser(String id);
    UserDTO findUserById(String id);
    Iterable<UserDTO> findAll();

    Optional<UserDTO> findByUsername(String username);


    void processOAuthPostLogin(String username, String email);

    Optional<UserDTO> findUserFromContext();

    Page<PaymentMethodDTO> getPaymentMethodsByUserId(UserDTO userDTO, Pageable page);
}
