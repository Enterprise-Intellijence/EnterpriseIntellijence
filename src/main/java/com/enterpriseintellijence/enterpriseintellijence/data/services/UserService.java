package com.enterpriseintellijence.enterpriseintellijence.data.services;

import com.enterpriseintellijence.enterpriseintellijence.data.entities.User;
import com.enterpriseintellijence.enterpriseintellijence.dto.UserDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.Provider;

public interface UserService {
    UserDTO createUser(UserDTO userDTO);
    UserDTO replaceUser(String id, UserDTO userDTO);
    UserDTO updateUser(String id, UserDTO userDTO);
    void deleteUser(String id);
    UserDTO userById(String id);
    Iterable<UserDTO> findAll();

    UserDTO userByUsername(String username);


    void processOAuthPostLogin(String username, String email);

}
