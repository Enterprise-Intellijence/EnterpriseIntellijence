package com.enterpriseintellijence.enterpriseintellijence.security;

import com.enterpriseintellijence.enterpriseintellijence.data.repository.UserRepository;
import com.enterpriseintellijence.enterpriseintellijence.data.services.CustomOAuth2UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;


@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class AppSecurityConfig {


//    @Bean
//    CorsConfigurationSource corsConfigurationSource() {
//        CorsConfiguration configuration = new CorsConfiguration();
//        configuration.setAllowedOrigins(Arrays.asList("http://localhost:4200"));
//        configuration.setAllowedMethods(Arrays.asList("GET","POST","DELETE","OPTIONS"));
//        configuration.setAllowedHeaders(Arrays.asList("Access-Control-Allow-Credentials", "Content-type", "Access-control-Allow-Origin", "Authorization" , "X-Requested-With", "Access-Control-Request-Method", "Access-Control-Request-Headers"));
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        configuration.setAllowCredentials(true);
//        source.registerCorsConfiguration("/**", configuration);
//        return source;
//    }


    //    /oauth2/authorization/google

    private final UserRepository userRepository;
    @Autowired
    private CustomOAuth2UserService oauthUserService;
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable().authorizeHttpRequests()
                .requestMatchers("/oauth2/authorization/google", "/oauth/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .oauth2Login()
                .userInfoEndpoint()
                .userService(oauthUserService)
                .and()
                .successHandler(new AuthenticationSuccessHandler() {

                    @Override
                    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                                        Authentication authentication) throws IOException, ServletException {

                        CustomOAuth2User oauthUser = (CustomOAuth2User) authentication.getPrincipal();
                        CustomOAuth2UserService userService = new CustomOAuth2UserService(userRepository);
                        userService.processOAuthPostLogin( oauthUser.getName(), oauthUser.getEmail());

                        response.sendRedirect("/");
                    }
                });
        return http.build();

    }
}

