package com.enterpriseintellijence.enterpriseintellijence.data.services;

import com.enterpriseintellijence.enterpriseintellijence.data.entities.User;
import com.enterpriseintellijence.enterpriseintellijence.data.repository.PaymentMethodRepository;
import com.enterpriseintellijence.enterpriseintellijence.data.repository.UserRepository;
import com.enterpriseintellijence.enterpriseintellijence.dto.PaymentMethodDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.UserDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.Provider;
import com.enterpriseintellijence.enterpriseintellijence.security.JwtContextUtils;
import com.enterpriseintellijence.enterpriseintellijence.exception.IdMismatchException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImp implements UserService{

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final JwtContextUtils jwtContextUtils;
    private final PaymentMethodRepository paymentMethodRepository;


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

    public UserDTO updateUser(String id, UserDTO patch) throws IllegalAccessException {
        UserDTO user = mapToDto(userRepository.findById(id).orElseThrow(EntityNotFoundException::new));
        UserDTO userContext = findUserFromContext().orElseThrow(EntityNotFoundException::new);

        if(userContext.getId() != patch.getId())
            throw new IllegalAccessException("User cannot change another user");

        if(userContext.getProvider() != patch.getProvider())
            throw new IllegalAccessException("User cannot change provider");

        if(patch.getUsername() != null)
            user.setUsername(patch.getUsername());

        if(patch.getEmail() != null)
            user.setEmail(patch.getEmail());

        user.setAddress(patch.getAddress());
        user.setPhoto(patch.getPhoto());
        userRepository.save(mapToEntity(user));
        return user;
    }

    public void  deleteUser(String id) {
        User user = userRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        userRepository.deleteById(id);

    }

    public UserDTO findUserById(String id) {
        User user = userRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        return mapToDto(user);
    }

    public Optional<UserDTO> findByUsername(String username) {
        User user= userRepository.findByUsername(username);
        if (user==null)
            return Optional.empty();
        return Optional.of(mapToDto(user));
    }

    public Iterable<UserDTO> findAll() {
        // TODO: Da implementare quando abbiamo l'admin
        return userRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }


    public void processOAuthPostLogin(String username, String email) {
        var existUser = findByUsername(username);

        if (existUser.isEmpty()) {
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
        if (username.isEmpty())
            return Optional.empty();

        return findByUsername(username.get());
    }

    @Override
    public Page<PaymentMethodDTO> getPaymentMethodsByUserId(UserDTO userDTO, Pageable page) {
        return paymentMethodRepository.findAllByDefaultUser_Id(userDTO.getId(), page)
                .map(paymentMethod -> modelMapper.map(paymentMethod, PaymentMethodDTO.class));
    }

    public void throwOnIdMismatch(String id, UserDTO userDTO){
        if(userDTO.getId() != null && !userDTO.getId().equals(id))
            throw new IdMismatchException();
    }




    public User mapToEntity(UserDTO userDTO){return modelMapper.map(userDTO, User.class);}
    public UserDTO mapToDto(User user){return modelMapper.map(user,UserDTO.class);}

}
