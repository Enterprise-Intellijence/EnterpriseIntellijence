package com.enterpriseintellijence.enterpriseintellijence.security;

import com.enterpriseintellijence.enterpriseintellijence.data.services.CustomUserDetailsService;
import com.nimbusds.oauth2.sdk.http.HTTPRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class AppSecurityConfig  {

    private final CustomUserDetailsService userDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    private final RequestFilter requestFilter;
    private final TokenStore tokenStore;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, AuthenticationManager authenticationManager) throws Exception {
        return http.csrf().disable()
                .authorizeHttpRequests().requestMatchers("/api/v1/admin/**", "/api/v1/reports/close/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_SUPER_ADMIN")
                .and()
                .authorizeHttpRequests().requestMatchers(HttpMethod.GET,"/api/v1/reports").hasAnyAuthority("ROLE_ADMIN", "ROLE_SUPER_ADMIN")
                .and()
                .authorizeHttpRequests().requestMatchers("/api/v1/superAdmin/**").hasAuthority("ROLE_SUPER_ADMIN")
                .and()
                .authorizeHttpRequests().requestMatchers(HttpMethod.GET, "/api/v1/products/**").permitAll()
                .and()
                .authorizeHttpRequests().requestMatchers(HttpMethod.GET, "/api/v1/products/capability/url/**").authenticated()
                .and()
                .authorizeHttpRequests().requestMatchers(HttpMethod.GET,"/api/v1/users/{id}","/api/v1/users/find-by-username","/api/v1/users/followers/{id}","/api/v1/users/following/{id}").permitAll()
                .and()
                .authorizeHttpRequests().requestMatchers(HttpMethod.GET,"/api/v1/products/{id}","/api/v1/products/filter","/api/v1/products/wardrobe").permitAll()
                .and()
                .authorizeHttpRequests().requestMatchers("/api/v1/demo","/api/v1/users/register", "/api/v1/users/authenticate",
                        "/api/v1/users/refreshToken", "/api/v1/users/google_auth","swagger-ui/**","/v3/api-docs/**","/api/v1/images/users/photo-profile","/api/v1/images/product",
                        "/api/v1/products"
                        /*"/api/v1/products/categories/**", "/api/v1/products/colour", "/api/v1/products/capability/**"*/).permitAll()
                .and()
                .authorizeHttpRequests().anyRequest().authenticated()
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilter(new CustomAuthenticationFilter(authenticationManager, tokenStore))
                .addFilterBefore(requestFilter, UsernamePasswordAuthenticationFilter.class)
                .oauth2Login()
                .and()
                .build();

    }


/*
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, AuthenticationManager authenticationManager) throws Exception {
        return http.csrf().disable()
                .authorizeHttpRequests().requestMatchers("/api/v1/users/register", "/api/v1/users/authenticate",
                        "/api/v1/users/refreshToken", "/api/v1/users/google_auth","swagger-ui/**","/v3/api-docs/**",
                        "/api/v1/products/categories/**", "/api/v1/products/colour", "/api/v1/products/capability/**").permitAll()
                .and()
                .authorizeHttpRequests().anyRequest().authenticated()
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilter(new CustomAuthenticationFilter(authenticationManager))
                .addFilterBefore(requestFilter, UsernamePasswordAuthenticationFilter.class)
                .oauth2Login()
                .and()
                .oauth2Client()
                .and()
                .build();

    }
*/


}
