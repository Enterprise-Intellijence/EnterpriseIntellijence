package com.enterpriseintellijence.enterpriseintellijence.data.services;

import com.enterpriseintellijence.enterpriseintellijence.dto.MessageDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.PaymentMethodDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.UserDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.basics.OrderBasicDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.basics.PaymentMethodBasicDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.basics.ProductBasicDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.basics.UserBasicDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.UserRole;
import com.nimbusds.jose.JOSEException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.text.ParseException;
import java.util.Map;
import java.util.Optional;

public interface UserService {
    UserDTO createUser(UserDTO userDTO);
    UserDTO replaceUser(String id, UserDTO userDTO) throws IllegalAccessException;
    UserDTO updateUser(String id, UserDTO patch) throws IllegalAccessException;
    void deleteUser(String id);
    UserBasicDTO findUserById(String id);
    Page<UserDTO> findAll(int page, int size);

    Optional<UserDTO> findByUsername(String username);


    void processOAuthPostLogin(String username, String email);

    Optional<UserDTO> findUserFromContext();

    Map<String, String> refreshToken(String authorizationHeader) throws JOSEException, ParseException;

    void createUser(String username, String password, String email);

    Page<UserBasicDTO> getFollowersByUserId(String userId, int page, int size);

    Page<UserBasicDTO> getFollowingByUserId(String userId, int page, int size);

    void followUser(String userIdToFollow);

    void unfollowUser(String userIdToUnfollow);

    void addLikeToProduct(String productId);

    void removeLikeFromProduct(String productId);

    Page<ProductBasicDTO> getProductLikedByUser(int page, int size);

    Page<OrderBasicDTO> getMyOrders(int page, int size);


    UserDTO changeRole(String userId, UserRole role);

    UserDTO banUser(String userId);

    UserDTO unBanUser(String userId);

    Page<PaymentMethodBasicDTO> getMyPaymentMethods(int page, int size);

    Page<MessageDTO> getMyInBoxMessage(int page, int size);

    Page<MessageDTO> getMyOutBoxMessage(int page, int size);

}
