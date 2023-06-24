package com.enterpriseintellijence.enterpriseintellijence.data.services;

import com.enterpriseintellijence.enterpriseintellijence.data.entities.User;
import com.enterpriseintellijence.enterpriseintellijence.dto.AddressDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.MessageDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.UserDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.basics.*;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.UserRole;
import com.nimbusds.jose.JOSEException;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.text.ParseException;
import java.util.Map;
import java.util.Optional;

public interface UserService {
    UserDTO createUser(User user);
    UserDTO replaceUser(String id, UserDTO userDTO) throws IllegalAccessException;
    UserDTO updateUser(String id, UserDTO patch) throws IllegalAccessException;
    void deleteUser(String id);
    UserBasicDTO findUserById(String id);
    Page<UserDTO> findAll(int page, int size);

    Optional<UserDTO> findByUsername(String username);


    void processOAuthPostLogin(String username, String email);

    Map<String, String> authenticateUser(String username, String password) throws JOSEException;

    ResponseEntity<String> registerUser(String username, String email, String password);

    ResponseEntity<String> sendVerificationEmail(String username) throws MessagingException;

    Map<String, String> refreshToken(String authorizationHeader, HttpServletResponse response) throws IOException;

    void logout(String authorizationHeader) throws ParseException, JOSEException;

    Optional<UserDTO> findUserFromContext();


    void createUser(String username, String password, String email);

    void activateUser(String token) throws ParseException, JOSEException;

/*    Page<UserBasicDTO> getFollowersByUserId(String userId, int page, int size);

    Page<UserBasicDTO> getFollowingByUserId(String userId, int page, int size);

    void followUser(String userIdToFollow);

    void unfollowUser(String userIdToUnfollow);*/

    void addLikeToProduct(String productId);

    void removeLikeFromProduct(String productId);

    Page<ProductBasicDTO> getProductLikedByUser(int page, int size);

    Page<OrderBasicDTO> getMyOrders(int page, int size);


    UserDTO changeRole(String userId, UserRole role);

    UserDTO banUser(String userId);

    UserDTO unBanUser(String userId);

/*    Page<MessageDTO> getMyInBoxMessage(int page, int size);

    Page<MessageDTO> getMyOutBoxMessage(int page, int size);*/

    Page<OfferBasicDTO> getMyOffers(int page, int size);

    void changePassword(String oldPassword, String newPassword, String authToken) throws ParseException, JOSEException, MessagingException;

    void changePassword(String token, String authToken) throws ParseException, JOSEException, MessagingException;

    void resetPassword(String email) throws MessagingException;

    UserDTO findMyProfile();

    AddressDTO getDefaultAddress(String userId);
}
