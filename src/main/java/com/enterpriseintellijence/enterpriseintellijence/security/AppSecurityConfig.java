package com.enterpriseintellijence.enterpriseintellijence.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;


@EnableWebSecurity
@Configuration
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

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable().authorizeHttpRequests().anyRequest().permitAll();
        return http.build();
    }

}
