package com.enterpriseintellijence.enterpriseintellijence.data.services;

import com.enterpriseintellijence.enterpriseintellijence.data.entities.User;
import com.enterpriseintellijence.enterpriseintellijence.data.repository.UserRepository;
import com.enterpriseintellijence.enterpriseintellijence.dto.ProductDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.UserDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.Provider;
import com.enterpriseintellijence.enterpriseintellijence.security.JwtContextUtils;
import com.enterpriseintellijence.enterpriseintellijence.exception.IdMismatchException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImp implements UserService{

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final JwtContextUtils jwtContextUtils;


    public UserDTO createUser(UserDTO userDTO) {
        User user = mapToEntity(userDTO);
        user = userRepository.save(user);
        return mapToDto(user);
    }

    public UserDTO replaceUser(String id, UserDTO userDTO) throws IllegalAccessException {
        //TODO: serve effettivamente questo metodo?
        throwOnIdMismatch(id,userDTO);
        User oldUser = userRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        User newUser = mapToEntity(userDTO);

        //todo: get user from context
        User requestingUser = new User();

        if(!requestingUser.getId().equals(oldUser.getId())) {
            throw new IllegalAccessException("same User");
        }
        if(!requestingUser.getId().equals(newUser.getId())) {
            throw new IllegalAccessException("same user");
        }

        newUser = userRepository.save(newUser);
        return mapToDto(newUser);
    }

    public UserDTO updateUser(String id, JsonPatch patch) throws JsonPatchException {
        UserDTO user = mapToDto(userRepository.findById(id).orElseThrow(EntityNotFoundException::new));
        user = applyPatch(patch,mapToEntity(user));
        userRepository.save(mapToEntity(user));
        return user;
    }

    public void  deleteUser(String id) {
        User user = userRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        userRepository.deleteById(id);
        //todo: perchè ritornava un DTO
    }

    public UserDTO findUserById(String id) {
        User user = userRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        return mapToDto(user);
    }

    public UserDTO findUserByUsername(String username) {
        return mapToDto(userRepository.findByUsername(username));
    }

    public Iterable<UserDTO> findAll() {
        // TODO: Da implementare quando abbiamo l'admin
        return userRepository.findAll().stream()
                .map(s -> mapToDto(s))
                .collect(Collectors.toList());
    }


    public void processOAuthPostLogin(String username, String email) {
        UserDTO existUser = findUserByUsername(username);

        if (existUser == null) {
            UserDTO newUser = new UserDTO();
            newUser.setUsername(username);
            newUser.setProvider(Provider.GOOGLE);
            newUser.setEmail(email);
            createUser(newUser);
        }

    }

    @Override
    public Optional<UserDTO> findUserFromContext() {
        Optional<String> username = jwtContextUtils.getUsernameFromContext();
        return username.map(this::findUserByUsername);
    }

    public void throwOnIdMismatch(String id, UserDTO userDTO){
        if(userDTO.getId() != null && !userDTO.getId().equals(id))
            throw new IdMismatchException();
    }

    public UserDTO applyPatch(JsonPatch patch, User user) throws JsonPatchException{
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode patched = patch.apply(objectMapper.convertValue(user,JsonNode.class));

        return objectMapper.convertValue(patched,UserDTO.class);
    }

    public User mapToEntity(UserDTO userDTO){return modelMapper.map(userDTO, User.class);}
    public UserDTO mapToDto(User user){return modelMapper.map(user,UserDTO.class);}

}
