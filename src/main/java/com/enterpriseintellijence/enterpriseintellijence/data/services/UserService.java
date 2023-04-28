package com.enterpriseintellijence.enterpriseintellijence.data.services;

import com.enterpriseintellijence.enterpriseintellijence.dto.UserDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.Provider;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;

import java.util.Optional;

public interface UserService {
    UserDTO createUser(UserDTO userDTO);
    UserDTO replaceUser(String id, UserDTO userDTO) throws IllegalAccessException;
    UserDTO updateUser(String id, JsonPatch patch) throws JsonPatchException;
    void deleteUser(String id);
    UserDTO findUserById(String id);
    Iterable<UserDTO> findAll();

    UserDTO findUserByUsername(String username);


    void processOAuthPostLogin(String username, String email);

    Optional<UserDTO> findUserFromContext();
}
