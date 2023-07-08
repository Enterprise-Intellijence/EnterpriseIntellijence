package com.enterpriseintellijence.enterpriseintellijence.security;
import com.enterpriseintellijence.enterpriseintellijence.data.repository.InvalidTokensRepository;
import com.enterpriseintellijence.enterpriseintellijence.data.services.CustomUserDetailsService;
import com.enterpriseintellijence.enterpriseintellijence.exception.TokenExpiredException;
import com.nimbusds.jose.JOSEException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.text.ParseException;

@Component
@RequiredArgsConstructor
@Slf4j
public class RequestFilter extends OncePerRequestFilter {

    private final CustomUserDetailsService userDetailsService;
    private final TokenStore tokenStore;
    private final InvalidTokensRepository invalidTokensRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        String token = tokenStore.getToken(request);
        if(!token.equals("invalid") && invalidTokensRepository.findByToken(token).isPresent()) {
            token = "invalid";
        }
        if(!"invalid".equals(token)) {
            try {
                String username = tokenStore.getUser(token);
                UserDetails user = userDetailsService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            } catch (Exception e) {
                if (e  instanceof TokenExpiredException)
                    response.addHeader("token_expired", "true");
                else
                    e.printStackTrace();
            }
        }
        chain.doFilter(request, response);
    }
}
