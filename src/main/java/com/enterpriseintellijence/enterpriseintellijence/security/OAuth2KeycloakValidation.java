package com.enterpriseintellijence.enterpriseintellijence.security;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.crypto.DefaultJwtSignatureValidator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Map;

@Component
public class OAuth2KeycloakValidation {

    @Value("${keycloak.issuer}")
    private String issuer;
    public Map<String, String> validate(String token) throws Exception {
        String[] chunks = token.split("\\.");
        Base64.Decoder decoder = Base64.getUrlDecoder();
        ObjectMapper objectMapper = new ObjectMapper();

        String header = new String(decoder.decode(chunks[0]));
        String payload = new String(decoder.decode(chunks[1]));
        
        String tokenWithoutSignature = chunks[0] + "." + chunks[1];
        String signature = chunks[2];

        if (!header.contains("HS256")) {
            throw new Exception("Invalid JWT token! Signature algorithm is not valid");
        }

        if (!payload.contains("iss") || !payload.contains(issuer)) {
            throw new Exception("Invalid JWT token! Issuer is not valid");
        }

        if (!payload.contains("email") || !payload.contains("name")) {
            throw new Exception("Invalid JWT token! Email or name is not valid");
        }

        JsonNode jsonNode = objectMapper.readTree(payload);

        Map<String, String> parsedPayload = Map.of("email", jsonNode.get("email").asText(), "name", jsonNode.get("name").asText());
        return parsedPayload;
    }
}
