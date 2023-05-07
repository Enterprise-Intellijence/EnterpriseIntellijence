package com.enterpriseintellijence.enterpriseintellijence.security;
import com.enterpriseintellijence.enterpriseintellijence.data.services.CustomUserDetailsService;
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

@Component
@RequiredArgsConstructor
@Slf4j
public class RequestFilter extends OncePerRequestFilter {

    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        String token = TokenStore.getInstance().getToken(request);
        log.info("token: {}", token);
        log.info("request: {}", request);
        if(!"invalid".equals(token)) {
            try {
                String username = TokenStore.getInstance().getUser(token);
                UserDetails user = userDetailsService.loadUserByUsername(username);
                log.info("user: {} --- roles: {}", user, user.getAuthorities());
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        chain.doFilter(request, response);
    }
}
