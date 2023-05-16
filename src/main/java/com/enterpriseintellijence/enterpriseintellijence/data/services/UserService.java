package com.enterpriseintellijence.enterpriseintellijence.data.services;

import com.enterpriseintellijence.enterpriseintellijence.dto.PaymentMethodDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.UserFullDTO;
import com.nimbusds.jose.JOSEException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.text.ParseException;
import java.util.Map;
import java.util.Optional;

public interface UserService {
    UserFullDTO createUser(UserFullDTO userFullDTO);
    UserFullDTO replaceUser(String id, UserFullDTO userFullDTO) throws IllegalAccessException;
    UserFullDTO updateUser(String id, UserFullDTO patch) throws IllegalAccessException;
    void deleteUser(String id);
    UserFullDTO findUserById(String id);
    Iterable<UserFullDTO> findAll();

    Optional<UserFullDTO> findByUsername(String username);


    void processOAuthPostLogin(String username, String email);

    Optional<UserFullDTO> findUserFromContext();

    Map<String, String> refreshToken(String authorizationHeader) throws JOSEException, ParseException;

    Page<PaymentMethodDTO> getPaymentMethodsByUserId(UserFullDTO userFullDTO, Pageable page);
}
