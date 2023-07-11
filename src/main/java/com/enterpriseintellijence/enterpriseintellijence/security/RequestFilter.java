package com.enterpriseintellijence.enterpriseintellijence.security;
import com.enterpriseintellijence.enterpriseintellijence.data.repository.InvalidTokensRepository;
import com.enterpriseintellijence.enterpriseintellijence.data.services.CustomUserDetailsService;
import com.enterpriseintellijence.enterpriseintellijence.exception.TokenExpiredException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.nimbusds.jose.JOSEException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import org.slf4j.Logger;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.CacheResponse;
import java.text.ParseException;

@Component
@RequiredArgsConstructor
@Slf4j
public class RequestFilter extends OncePerRequestFilter {

    private static final Logger logger = org.slf4j.LoggerFactory.getLogger("myLogger");

    private final CustomUserDetailsService userDetailsService;
    private final TokenStore tokenStore;
    private final InvalidTokensRepository invalidTokensRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {

        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);
        long startTime = System.currentTimeMillis();
        String loggedUser = "";

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
                loggedUser = user.getUsername();
            } catch (Exception e) {
                if (e  instanceof TokenExpiredException)
                    response.addHeader("token_expired", "true");
                else
                    e.printStackTrace();
            }
        }

        chain.doFilter (requestWrapper, responseWrapper);

        long timeTaken = System.currentTimeMillis () - startTime;
        String requestBody = getStringValue(requestWrapper.getContentAsByteArray(),
                request.getCharacterEncoding());
        String responseBody = getStringValue(responseWrapper.getContentAsByteArray(),
                response.getCharacterEncoding());
        logger.info(String.format("\nFINISHED PROCESSING : METHOD={%s}; REQUESTURI={%s}; REMOTEADDR={%S};\nREQUEST PAYLOAD={%s};\nLOGGED USERNAME={%s}; RESPONSE CODE={%d};\nRESPONSE={%s}; TIME TAKEN={%d}",
                request.getMethod(), request.getRequestURI(), request.getRemoteAddr(), requestBody, loggedUser,response.getStatus(), responseBody, timeTaken));
        responseWrapper.copyBodyToResponse();
    }

    private String getStringValue (byte[] contentAsByteArray, String characterEncoding) {
        if (contentAsByteArray == null || contentAsByteArray.length == 0) {
            return "";
        }
        try {
            String jsonString = new String(contentAsByteArray, characterEncoding);
            ObjectMapper objectMapper = new ObjectMapper();
            Object jsonObject = objectMapper.readValue(jsonString, Object.class);
            ObjectWriter objectWriter = objectMapper.writerWithDefaultPrettyPrinter();
            return objectWriter.writeValueAsString(jsonObject);
        } catch (JsonProcessingException ignored) {
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        return "";
    }
}
