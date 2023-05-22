package com.enterpriseintellijence.enterpriseintellijence.security;

import com.enterpriseintellijence.enterpriseintellijence.data.entities.User;
import com.enterpriseintellijence.enterpriseintellijence.data.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class JwtContextUtils {

    private final UserRepository userRepository;

    //USELESS CLASS, USE TokenStore.getUser(token) INSTEAD

    public Optional<String> getUsernameFromContext() {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        if (authentication == null) {
            return Optional.empty();
        }

        if(authentication.getPrincipal() instanceof UserDetails userDetails) {
            return Optional.of(userDetails.getUsername());
        } else if(authentication.getPrincipal() instanceof CustomOAuth2User customOAuth2User) {
            return Optional.of(customOAuth2User.getUsername());
        }

        return Optional.empty();
    }

    public User getUserLoggedFromContext(){
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        if(authentication==null)
            return null;
        if(authentication.getPrincipal() instanceof UserDetails userDetails){
            return userRepository.findByUsername(userDetails.getUsername());
        }
        else if(authentication.getPrincipal() instanceof CustomOAuth2User customOAuth2User) {
            return userRepository.findByUsername(customOAuth2User.getUsername());
        }
        return null;
    }
}
