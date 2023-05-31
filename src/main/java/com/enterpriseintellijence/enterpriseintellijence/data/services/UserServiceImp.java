package com.enterpriseintellijence.enterpriseintellijence.data.services;

import com.enterpriseintellijence.enterpriseintellijence.data.entities.*;
import com.enterpriseintellijence.enterpriseintellijence.data.entities.embedded.Address;
import com.enterpriseintellijence.enterpriseintellijence.data.repository.PaymentMethodRepository;
import com.enterpriseintellijence.enterpriseintellijence.data.repository.ProductRepository;
import com.enterpriseintellijence.enterpriseintellijence.data.repository.UserRepository;
import com.enterpriseintellijence.enterpriseintellijence.dto.MessageDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.UserDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.basics.*;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.Provider;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.UserRole;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.UserStatus;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.Visibility;
import com.enterpriseintellijence.enterpriseintellijence.security.Constants;
import com.enterpriseintellijence.enterpriseintellijence.security.JwtContextUtils;
import com.enterpriseintellijence.enterpriseintellijence.exception.IdMismatchException;
import com.enterpriseintellijence.enterpriseintellijence.security.TokenStore;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JOSEException;
import jakarta.persistence.*;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Map.of;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImp implements UserService{

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final JwtContextUtils jwtContextUtils;
    private final PaymentMethodRepository paymentMethodRepository;
    private final ProductRepository productRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TokenStore tokenStore;
    private final EmailService emailService;



    public UserDTO createUser(User user) {
        user.setStatus(UserStatus.ACTIVE);
        user = userRepository.save(user);
        return mapToDto(user);
    }

    public UserDTO replaceUser(String id, UserDTO userDTO) throws IllegalAccessException {
        /*//throwOnIdMismatch(id, userDTO);
        User loggedUser = userRepository.findByUsername(jwtContextUtils.getUsernameFromContext().get());

        if(!id.equals(userDTO.getId()) || !id.equals(loggedUser.getId()) || !userDTO.getId().equals(loggedUser.getId()))
            throw new IllegalAccessException("User cannot change another user");

        User oldUser = userRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        userDTO.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        User newUser = mapToEntity(userDTO);

       User requestingUser = new User();

        if(!requestingUser.getId().equals(oldUser.getId())) {
            throw new IllegalAccessException("same User");
        }
        if(!requestingUser.getId().equals(newUser.getId())) {
            throw new IllegalAccessException("same user");
        }


        newUser = userRepository.save(newUser);
        return mapToDto(newUser);*/
        return updateUser(id,userDTO);
    }

    public UserDTO updateUser(String id, UserDTO userDTO) throws IllegalAccessException {
        User loggedUser = userRepository.findByUsername(jwtContextUtils.getUsernameFromContext().get());
        User oldUser = userRepository.findById(id).orElseThrow();

        if(!id.equals(userDTO.getId()))
            throw new IllegalAccessException("User cannot change another user");

        if(!id.equals(loggedUser.getId()) && (!loggedUser.getRole().equals(UserRole.ADMIN)) )
            throw new IllegalAccessException("User cannot change another user");

        userDTO.setPassword(passwordEncoder.encode(userDTO.getPassword()));

        // TODO: 21/05/2023 testare se controlla da solo se username/email sono disponibili e se restituisce errore
        if(userDTO.getUsername()!=null && !oldUser.getUsername().equals(userDTO.getUsername()))
            oldUser.setUsername(userDTO.getUsername());
        if(userDTO.getPassword()!=null && !oldUser.getPassword().equals(userDTO.getPassword()))
            oldUser.setPassword(userDTO.getPassword());
        if(userDTO.getEmail()!=null && !oldUser.getEmail().equals(userDTO.getEmail()))
            oldUser.setEmail(userDTO.getEmail());
        if(userDTO.getPhotoProfile()!=null ){
            UserImage userImage = new UserImage();
            if(oldUser.getPhotoProfile().getId()!=null)
                userImage.setId(oldUser.getId());
            if(userDTO.getPhotoProfile().getDescription()!=null && !oldUser.getPhotoProfile().getDescription().equals(userDTO.getPhotoProfile().getDescription()))
                userImage.setDescription(userDTO.getPhotoProfile().getDescription());
            /*if(userDTO.getPhotoProfile().getPhoto()!=null && !Arrays.equals(oldUser.getPhotoProfile().getPhoto(), userDTO.getPhotoProfile().getPhoto()))
                userImage.setPhoto(userDTO.getPhotoProfile().getPhoto());
*/          userImage.setUser(oldUser);
            oldUser.setPhotoProfile(userImage);
        }
        oldUser.setAddress(modelMapper.map( userDTO.getAddress(),Address.class));
        if (userDTO.getDefaultPaymentMethod()!=null &&  !oldUser.getDefaultPaymentMethod().getId().equals(userDTO.getDefaultPaymentMethod().getId())) {
            PaymentMethod paymentMethod = paymentMethodRepository.getReferenceById(userDTO.getDefaultPaymentMethod().getId());
            paymentMethod.setDefaultUser(oldUser);
            oldUser.setDefaultPaymentMethod(paymentMethod);
        }
        userRepository.save(oldUser);
        return mapToDto(oldUser);
    }

    public void  deleteUser(String id) {
        User user = userRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        userRepository.deleteById(id);
    }

    public UserBasicDTO findUserById(String id) {
        User user = userRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        if (!user.getStatus().equals(UserStatus.ACTIVE))
            throw new EntityNotFoundException();
        return mapToBasicDto(user);
    }

    public Optional<UserDTO> findByUsername(String username) {
        User user= userRepository.findByUsername(username);
        if (user==null || !user.getStatus().equals(UserStatus.ACTIVE))
            return Optional.empty();
        return Optional.of(mapToDto(user));
    }

    public Page<UserDTO> findAll(int page, int size) {
        // TODO: Da implementare quando abbiamo l'admin
        return userRepository.findAll(PageRequest.of(page, size))
                .map(this::mapToDto);
    }


    public void processOAuthPostLogin(String username, String email) {
        var existUser = findByUsername(username);

        if (existUser.isEmpty()) {
            User newUser = new User();
            newUser.setUsername(username);
            newUser.setProvider(Provider.GOOGLE);
            newUser.setEmail(email);
            createUser(newUser);
        }

    }

    @Override
    public Map<String, String> authenticateUser(String username, String password) throws JOSEException {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        String accessToken = tokenStore.createAccessToken(Map.of("username", username, "role", "user"));
        String refreshToken = tokenStore.createRefreshToken(username);
        return Map.of("accessToken", accessToken, "refreshToken", refreshToken);
    }

    @Override
    public ResponseEntity<String> registerUser(String username, String email, String password) {
        if(findByUsername(username).isPresent())
            return new ResponseEntity<>( "existing username" , HttpStatus.CONFLICT);
        createUser(username, passwordEncoder.encode(password), email);
        log.info("User created: " + username);
        return new ResponseEntity<>( "user created" , HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<String> sendVerificationEmail(String username) {
        User user = userRepository.findByUsername(username);
        if(user == null)
            return new ResponseEntity<>( "user not found" , HttpStatus.NOT_FOUND);
        if(user.isEmailVerified())
            return new ResponseEntity<>( "user already verified" , HttpStatus.CONFLICT);
        String token = tokenStore.createEmailToken(username);
        String url = "https://localhost:8443/api/v1/users/activate?token=" + token;
        emailService.sendEmail(user.getEmail(), Constants.VERIFICATION_EMAIL_SUBJECT,Constants.VERIFICATION_EMAIL_TEXT + url);
        return new ResponseEntity<>( "verification email sent" , HttpStatus.OK);
    }

    @Override
    public void refreshToken(String authorizationHeader, HttpServletResponse response) throws IOException {
        if(authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            try {
                String refreshToken = authorizationHeader.substring("Bearer ".length());
                String username = tokenStore.getUser(refreshToken);
                UserDTO user = findByUsername(username).orElseThrow(()->new RuntimeException("user not found"));

                User userDetails = mapToEntity(user);
                String accessToken = tokenStore.createAccessToken(Map.of("username", userDetails.getUsername(), "role", userDetails.getRole()));;
                response.addHeader(AUTHORIZATION, "Bearer " + accessToken);
                response.addHeader("refresh_token", "Bearer " + refreshToken);
            }
            catch (Exception e) {
                log.error(String.format("Error refresh token: %s", authorizationHeader), e);
                response.setStatus(FORBIDDEN.value());
                Map<String, String> error = new HashMap<>();
                error.put("errorMessage", e.getMessage());
                response.setContentType(APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), error);
            }
        } else {
            throw new RuntimeException("Refresh token is missing");
        }
    }

    @Override
    public void logout(String authorizationHeader) throws ParseException, JOSEException {
        if(authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String accessToken = authorizationHeader.substring("Bearer ".length());
            tokenStore.logout(accessToken);
        } else {
            throw new RuntimeException("Refresh token is missing");
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

    public void throwOnIdMismatch(String id, UserDTO userDTO){
        if(userDTO.getId() != null && !userDTO.getId().equals(id))
            throw new IdMismatchException();
    }

    @Override
    public void createUser(String username, String password, String email) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);
        user.setRole(UserRole.USER);
        user.setProvider(Provider.LOCAL);
        user.setFollowers_number(0);
        user.setFollowing_number(0);
        user.setReviews_total_sum(0);
        user.setReviews_number(0);
        user.setEmailVerified(false);
        createUser(user);
    }

    @Override
    public void activateUser(String token) throws ParseException, JOSEException {
        String username = tokenStore.getUser(token);

        User user = userRepository.findByUsername(username);
        user.setEmailVerified(true);
        userRepository.save(user);

        new ResponseEntity<>("user activated", HttpStatus.OK);
    }

    @Override
    public Page<UserBasicDTO> getFollowersByUserId(String userId, int page, int size) {
        return userRepository.findAllByFollowingId(userId, PageRequest.of(page, size))
                .map(user -> modelMapper.map(user, UserBasicDTO.class));
    }

    @Override
    public Page<UserBasicDTO> getFollowingByUserId(String userId, int page, int size) {
        return userRepository.findAllByFollowersId(userId, PageRequest.of(page, size))
                .map(user -> modelMapper.map(user, UserBasicDTO.class));
    }

    @Override
    public void followUser(String userIdToFollow) {
        String username = jwtContextUtils.getUsernameFromContext().orElseThrow(EntityNotFoundException::new);
        String userId = userRepository.findByUsername(username).getId();

        User actualUser = userRepository.findById(userId).orElseThrow(EntityNotFoundException::new);

        //userRepository.addFollow(userId, userIdToFollow);

        User userToFollow = userRepository.findById(userIdToFollow).orElseThrow(EntityNotFoundException::new);

        userToFollow.getFollowers().add(actualUser);
        userToFollow.setFollowers_number(userToFollow.getFollowers_number()+1);

        actualUser.getFollowing().add(userToFollow);
        actualUser.setFollowing_number(actualUser.getFollowing_number()+1);
        userRepository.save(actualUser);
        //userRepository.save(userToFollow);

        /*
        userRepository.increaseFollowersNumber(userIdToFollow);
        userRepository.increaseFollowingNumber(userId);
        */
    }

    @Override
    public void unfollowUser(String userIdToUnfollow) {
        String username = jwtContextUtils.getUsernameFromContext().orElseThrow(EntityNotFoundException::new);
        User actualUser = userRepository.findByUsername(username);

        User userToUnfollow = userRepository.findById(userIdToUnfollow).orElseThrow(EntityNotFoundException::new);

        if(userToUnfollow.getFollowers().contains(actualUser)){
            userToUnfollow.getFollowers().remove(actualUser);
            userToUnfollow.setFollowers_number(userToUnfollow.getFollowers_number()-1);
        }

        if(actualUser.getFollowing().contains(userToUnfollow)){
            actualUser.getFollowing().remove(userToUnfollow);
            actualUser.setFollowing_number(actualUser.getFollowing_number()-1);
        }


        //userRepository.save(userToUnfollow);
        userRepository.save(actualUser);

        /*
        userRepository.findById(userId).orElseThrow(EntityNotFoundException::new);
        userRepository.findById(userIdToUnfollow).orElseThrow(EntityNotFoundException::new);

        userRepository.removeFollow(userId, userIdToUnfollow);*/
/*
        userRepository.decreaseFollowingNumbers(userId);
        userRepository.decreaseFollowersNumbers(userIdToUnfollow);
        */
    }

    @Override
    public void addLikeToProduct(String productId) {
        String username = jwtContextUtils.getUsernameFromContext().orElseThrow(EntityNotFoundException::new);
        //String userId = userRepository.findByUsername(username).getId();

        User user = userRepository.findByUsername(username);
        Product product = productRepository.findById(productId).orElseThrow(EntityNotFoundException::new);

        product.setLikesNumber(product.getLikesNumber()+1);
        user.getLikedProducts().add(product);
        //product.getUsersThatLiked(user);
        //userRepository.addLikeToProduct(userId, productId);
        //productRepository.increaseLikesNumber(productId);
        userRepository.save(user);
    }

    @Override
    public void removeLikeFromProduct(String productId) {
        String username = jwtContextUtils.getUsernameFromContext().orElseThrow(EntityNotFoundException::new);
        //String userId = userRepository.findByUsername(username).getId();

        User user = userRepository.findByUsername(username);
        Product product = productRepository.findById(productId).orElseThrow(EntityNotFoundException::new);

        if(user.getLikedProducts().contains(product)){
            product.setLikesNumber(product.getLikesNumber()-1);
            user.getLikedProducts().remove(product);
            userRepository.save(user);
        }

/*        userRepository.findById(userId).orElseThrow(EntityNotFoundException::new);
        productRepository.findById(productId).orElseThrow(EntityNotFoundException::new);

        userRepository.removeLikeToProduct(userId, productId);
        productRepository.decreaseLikesNumber(productId);*/
    }

    @Override
    public Page<ProductBasicDTO> getProductLikedByUser(int page, int size) {
        String username = jwtContextUtils.getUsernameFromContext().orElseThrow(EntityNotFoundException::new);
        User user = userRepository.findByUsername(username);

        /*return productRepository.findAllByUsersThatLiked(user, PageRequest.of(page, size))
                .map(product -> modelMapper.map(product, ProductBasicDTO.class));*/
        Page<Product> products =productRepository.findAllByVisibilityAndUsersThatLiked(Visibility.PUBLIC,user,PageRequest.of(page,size));
        List<ProductBasicDTO> collect = products.stream().map(s->modelMapper.map(s, ProductBasicDTO.class)).collect(Collectors.toList());
        return new PageImpl<>(collect);
    }

    @Override
    public Page<OrderBasicDTO> getMyOrders(int page, int size) {
        String username = jwtContextUtils.getUsernameFromContext().orElseThrow(EntityNotFoundException::new);
        User user = userRepository.findByUsername(username);
        Page<Order> orders = new PageImpl<Order>(user.getOrders(),PageRequest.of(page,size),user.getOrders().size());
        List<OrderBasicDTO> collect = orders.stream().map(s->modelMapper.map(s, OrderBasicDTO.class)).collect(Collectors.toList());

        return new PageImpl<>(collect);
    }

    @Override
    public UserDTO changeRole(String userId, UserRole role) {
        User user = userRepository.findById(userId).orElseThrow(EntityNotFoundException::new);
        user.setRole(role);
        return mapToDto(userRepository.save(user));
    }

    @Override
    public UserDTO banUser(String userId) {
        User user = userRepository.findById(userId).orElseThrow(EntityNotFoundException::new);
        if(user.getRole().equals(UserRole.ADMIN)){
            throw new IllegalArgumentException("Admins cannot be banned");
        }
        user.setStatus(UserStatus.BANNED);
        return mapToDto(userRepository.save(user));
    }

    @Override
    public UserDTO unBanUser(String userId) {
        User user = userRepository.findById(userId).orElseThrow(EntityNotFoundException::new);

        user.setStatus(UserStatus.ACTIVE);
        return mapToDto(userRepository.save(user));
    }
    public Page<PaymentMethodBasicDTO> getMyPaymentMethods(int page, int size) {
        String username = jwtContextUtils.getUsernameFromContext().orElseThrow(EntityNotFoundException::new);
        User user = userRepository.findByUsername(username);
        Page<PaymentMethod> paymentMethods = new PageImpl<PaymentMethod>(user.getPaymentMethods(),PageRequest.of(page,size),user.getPaymentMethods().size());
        List<PaymentMethodBasicDTO> collect = paymentMethods.stream().map(s->modelMapper.map(s, PaymentMethodBasicDTO.class)).collect(Collectors.toList());

        return new PageImpl<>(collect);
    }

    @Override
    public Page<MessageDTO> getMyInBoxMessage(int page, int size) {
        String username = jwtContextUtils.getUsernameFromContext().orElseThrow(EntityNotFoundException::new);
        User user = userRepository.findByUsername(username);
        Page<Message> messages = new PageImpl<Message>(user.getReceivedMessages(),PageRequest.of(page,size),user.getReceivedMessages().size());
        List<MessageDTO> collect = messages.stream().map(s->modelMapper.map(s, MessageDTO.class)).collect(Collectors.toList());

        return new PageImpl<>(collect);
    }

    @Override
    public Page<MessageDTO> getMyOutBoxMessage(int page, int size) {
        String username = jwtContextUtils.getUsernameFromContext().orElseThrow(EntityNotFoundException::new);
        User user = userRepository.findByUsername(username);
        Page<Message> messages = new PageImpl<Message>(user.getSentMessages(),PageRequest.of(page,size),user.getSentMessages().size());
        List<MessageDTO> collect = messages.stream().map(s->modelMapper.map(s, MessageDTO.class)).collect(Collectors.toList());

        return new PageImpl<>(collect);
    }

    @Override
    public Page<OfferBasicDTO> getMyOffers(int page, int size) {
        User loggedUser = jwtContextUtils.getUserLoggedFromContext();
        Page<Offer> offers = new PageImpl<Offer>(loggedUser.getOffersMade(),PageRequest.of(page,size),loggedUser.getOffersMade().size());
        List<OfferBasicDTO> collect = offers.stream().map(s->modelMapper.map(s, OfferBasicDTO.class)).collect(Collectors.toList());

        return new PageImpl<>(collect);
    }

    public User mapToEntity(UserDTO userDTO){return modelMapper.map(userDTO, User.class);}
    public UserDTO mapToDto(User user){return modelMapper.map(user, UserDTO.class);}
    public UserBasicDTO mapToBasicDto(User user){return modelMapper.map(user,UserBasicDTO.class);}



}
