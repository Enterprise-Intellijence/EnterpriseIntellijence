package com.enterpriseintellijence.enterpriseintellijence.security;

import com.nimbusds.jose.JOSEException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Map;

@RequiredArgsConstructor
public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        return super.attemptAuthentication(request, response);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                                            Authentication authentication) throws IOException, ServletException {

        UserDetails user = (UserDetails)authentication.getPrincipal();
        String accessToken = null;
        try {
            accessToken = TokenStore.getInstance().createAccessToken(Map.of("username", user.getUsername(), "role", user.getAuthorities().toString()));
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }
        String refreshToken = TokenStore.getInstance().createRefreshToken(user.getUsername());
        response.addHeader("access_token", accessToken);
        response.addHeader("refresh_token", refreshToken);
    }

}
