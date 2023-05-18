package com.enterpriseintellijence.enterpriseintellijence.data.services;

import com.enterpriseintellijence.enterpriseintellijence.data.entities.User;
import com.enterpriseintellijence.enterpriseintellijence.data.repository.PaymentMethodRepository;
import com.enterpriseintellijence.enterpriseintellijence.data.repository.ProductRepository;
import com.enterpriseintellijence.enterpriseintellijence.data.repository.UserRepository;
import com.enterpriseintellijence.enterpriseintellijence.dto.PaymentMethodDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.UserDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.basics.ProductBasicDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.basics.UserBasicDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.Provider;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.UserRole;
import com.enterpriseintellijence.enterpriseintellijence.security.JwtContextUtils;
import com.enterpriseintellijence.enterpriseintellijence.exception.IdMismatchException;
import com.enterpriseintellijence.enterpriseintellijence.security.TokenStore;
import com.nimbusds.jose.JOSEException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImp implements UserService{

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final JwtContextUtils jwtContextUtils;
    private final PaymentMethodRepository paymentMethodRepository;
    private final ProductRepository productRepository;


    public UserDTO createUser(UserDTO userDTO) {
        User user = mapToEntity(userDTO);
        user = userRepository.save(user);
        return mapToDto(user);
    }

    public UserDTO replaceUser(String id, UserDTO userDTO) throws IllegalAccessException {
        //TODO: serve effettivamente questo metodo?
        throwOnIdMismatch(id, userDTO);
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

        // TODO: 09/05/2023 occhio che quando memorizza una nuova password, non passa da bcrypt e la memorizza in chiaro...fixare 

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

    public UserBasicDTO findUserById(String id) {
        User user = userRepository.findById(id).orElseThrow(EntityNotFoundException::new);

        return mapToBasicDto(user);
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

        Optional<UserDTO> user = findByUsername(username.get());
        user.orElseThrow().setPassword(null);
        return user;
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

    @Override
    public Map<String, String> refreshToken(String authorizationHeader) throws ParseException, JOSEException {

        String refreshToken = authorizationHeader.substring("Bearer ".length());
        String username = TokenStore.getInstance().getUser(refreshToken);
        UserDTO user = findByUsername(username).orElseThrow(()->new RuntimeException("user not found"));

        User userDetails = mapToEntity(user);
        String accessToken = TokenStore.getInstance().createAccessToken(Map.of("username", userDetails.getUsername(), "role", userDetails.getRole()));
        return Map.of("access_token", accessToken, "refresh_token", refreshToken);

    }

    @Override
    public void createUser(String username, String password, String email) {
        UserDTO user = new UserDTO();
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);
        user.setRole(UserRole.USER);
        user.setProvider(Provider.LOCAL);
        user.setFollowers_number(0);
        user.setFollowing_number(0);

        createUser(user);
    }


    @Override
    public Page<UserBasicDTO> getFollowersByUserId(String userId, int page, int size) {
        return userRepository.findAllFollowers(userId, PageRequest.of(page, size))
                .map(user -> modelMapper.map(user, UserBasicDTO.class));
    }

    @Override
    public Page<UserBasicDTO> getFollowingByUserId(String userId, int page, int size) {
        return userRepository.findAllFollowing(userId, PageRequest.of(page, size))
                .map(user -> modelMapper.map(user, UserBasicDTO.class));
    }

    @Override
    public void followUser(String userIdToFollow) {
        String username = jwtContextUtils.getUsernameFromContext().orElseThrow(EntityNotFoundException::new);
        String userId = userRepository.findByUsername(username).getId();

        userRepository.findById(userIdToFollow).orElseThrow(EntityNotFoundException::new);

        userRepository.addFollow(userId, userIdToFollow);
        userRepository.increaseFollowersNumber(userIdToFollow);
        userRepository.increaseFollowingNumber(userId);
    }

    @Override
    public void unfollowUser(String userIdToUnfollow) {
        String username = jwtContextUtils.getUsernameFromContext().orElseThrow(EntityNotFoundException::new);
        String userId = userRepository.findByUsername(username).getId();

        userRepository.findById(userId).orElseThrow(EntityNotFoundException::new);
        userRepository.findById(userIdToUnfollow).orElseThrow(EntityNotFoundException::new);

        userRepository.removeFollow(userId, userIdToUnfollow);
        userRepository.decreaseFollowingNumbers(userId);
        userRepository.decreaseFollowersNumbers(userIdToUnfollow);
    }

    @Override
    public void addLikeToProduct(String productId) {
        String username = jwtContextUtils.getUsernameFromContext().orElseThrow(EntityNotFoundException::new);
        String userId = userRepository.findByUsername(username).getId();

        userRepository.findById(userId).orElseThrow(EntityNotFoundException::new);
        productRepository.findById(productId).orElseThrow(EntityNotFoundException::new);

        userRepository.addLikeToProduct(userId, productId);
        productRepository.increaseLikesNumber(productId);
    }

    @Override
    public void removeLikeFromProduct(String productId) {
        String username = jwtContextUtils.getUsernameFromContext().orElseThrow(EntityNotFoundException::new);
        String userId = userRepository.findByUsername(username).getId();

        userRepository.findById(userId).orElseThrow(EntityNotFoundException::new);
        productRepository.findById(productId).orElseThrow(EntityNotFoundException::new);

        userRepository.removeLikeToProduct(userId, productId);
        productRepository.decreaseLikesNumber(productId);
    }

    @Override
    public Page<ProductBasicDTO> getLikesByUserId(int page, int size) {
        String username = jwtContextUtils.getUsernameFromContext().orElseThrow(EntityNotFoundException::new);
        String userId = userRepository.findByUsername(username).getId();

        return userRepository.findAllLikedProducts(userId, PageRequest.of(page, size))
                .map(product -> modelMapper.map(product, ProductBasicDTO.class));
    }

    public User mapToEntity(UserDTO userDTO){return modelMapper.map(userDTO, User.class);}
    public UserDTO mapToDto(User user){return modelMapper.map(user, UserDTO.class);}
    public UserBasicDTO mapToBasicDto(User user){return modelMapper.map(user,UserBasicDTO.class);}

}
