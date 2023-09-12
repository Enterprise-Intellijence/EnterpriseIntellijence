package com.enterpriseintellijence.enterpriseintellijence.security;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Map;

@Component
public class OAuth2KeycloakValidation {
    public Map<String, String> validate(String token) throws Exception {
        System.out.println("token: " + token);
        String[] chunks = token.split("\\.");
        Base64.Decoder decoder = Base64.getUrlDecoder();
        ObjectMapper objectMapper = new ObjectMapper();

        String header = new String(decoder.decode(chunks[0]));
        String payload = new String(decoder.decode(chunks[1]));

        System.out.println("header: " + header);
        System.out.println("payload: " + payload);

        String tokenWithoutSignature = chunks[0] + "." + chunks[1];
        String signature = chunks[2];

        /*SignatureAlgorithm sa = SignatureAlgorithm.HS256;
        SecretKeySpec secretKeySpec = new SecretKeySpec(Constants.KEYCLOAK_SECRET_KEY.getBytes(), sa.getJcaName());

        DefaultJwtSignatureValidator validator = new DefaultJwtSignatureValidator(sa, secretKeySpec);

        if (!validator.isValid(tokenWithoutSignature, signature)) {
            throw new Exception("Could not verify JWT token integrity!");
        }*/

        JsonNode jsonNode = objectMapper.readTree(payload);

        Map<String, String> parsedPayload = Map.of("email", jsonNode.get("email").asText(), "name", jsonNode.get("name").asText());
        parsedPayload.forEach((k, v) -> System.out.println(k + ": " + v));
        return parsedPayload;
    }
}
