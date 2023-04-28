package com.enterpriseintellijence.enterpriseintellijence.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

public class JwtContextUtils {

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
}
