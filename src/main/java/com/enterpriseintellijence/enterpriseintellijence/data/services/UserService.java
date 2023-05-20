package com.enterpriseintellijence.enterpriseintellijence.data.services;

import com.enterpriseintellijence.enterpriseintellijence.dto.PaymentMethodDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.UserDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.basics.ProductBasicDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.basics.UserBasicDTO;
import org.hibernate.query.sqm.mutation.internal.cte.CteInsertStrategy;
import org.hibernate.query.sqm.mutation.internal.cte.CteMutationStrategy;
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
    Iterable<UserDTO> findAll();

    Optional<UserDTO> findByUsername(String username);


    void processOAuthPostLogin(String username, String email);

    Optional<UserDTO> findUserFromContext();

    Map<String, String> refreshToken(String authorizationHeader) throws JOSEException, ParseException;

    Page<PaymentMethodDTO> getPaymentMethodsByUserId(UserDTO userDTO, Pageable page);

    void createUser(String username, String password, String email);

    Page<UserBasicDTO> getFollowersByUserId(String userId, int page, int size);

    Page<UserBasicDTO> getFollowingByUserId(String userId, int page, int size);

    void followUser(String userIdToFollow);

    void unfollowUser(String userIdToUnfollow);

    void addLikeToProduct(String productId);

    void removeLikeFromProduct(String productId);

    Page<ProductBasicDTO> getProducLikedByUser(int page, int size);
}
