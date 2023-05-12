package com.enterpriseintellijence.enterpriseintellijence.security;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;

public class TokenStore {
    private final String secretKey = Constants.TOKEN_SECRET_KEY;

    private final static TokenStore instance = new TokenStore();

    private TokenStore() {}

    public static TokenStore getInstance() {
        return instance;
    }

    public String createAccessToken(Map<String, Object> claims) throws JOSEException {
        Instant issuedAt = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        Instant expiration = issuedAt.plus(Constants.JWT_EXPIRATION_TIME, ChronoUnit.HOURS);

        JWTClaimsSet.Builder builder = new JWTClaimsSet.Builder();
        for(String entry : claims.keySet())
            builder.claim(entry, claims.get(entry));
        JWTClaimsSet claimsSet = builder.issueTime(Date.from(issuedAt)).notBeforeTime(Date.from(issuedAt)).expirationTime(Date.from(expiration)).build();
        Payload payload = new Payload(claimsSet.toJSONObject());

        JWSObject jwsObject = new JWSObject(new JWSHeader(JWSAlgorithm.HS256), payload);
        jwsObject.sign(new MACSigner(secretKey.getBytes()));
        return jwsObject.serialize();
    }

    public boolean verifyToken(String token) throws JOSEException, ParseException {
        try {
            getUser(token);
            return true;
        } catch (RuntimeException e) {
            return false;
        }
    }

    public String getUser(String token) throws JOSEException, ParseException {
        SignedJWT signedJWT = SignedJWT.parse(token);
        JWSVerifier jwsVerifier = new MACVerifier(secretKey.getBytes());
        if(signedJWT.verify(jwsVerifier)) {
            if(new Date().before(signedJWT.getJWTClaimsSet().getExpirationTime()) && new Date().after(signedJWT.getJWTClaimsSet().getNotBeforeTime()))
                return (String) signedJWT.getPayload().toJSONObject().get("username");
        }
        throw new RuntimeException("Invalid token");
    }

    public String getToken(HttpServletRequest request) {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if(header != null && header.startsWith("Bearer "))
            return header.replace("Bearer ", "");
        return "invalid";
    }

    public String createRefreshToken(String username) {

        try {
            JWTClaimsSet claims = new JWTClaimsSet.Builder()
                    .subject(username)
                    .expirationTime(Date.from(Instant.now().plus(Constants.JWT_REFRESH_EXPIRATION_TIME, ChronoUnit.HOURS)))
                    .issueTime(new Date())
                    .build();

            Payload payload = new Payload(claims.toJSONObject());

            JWSObject jwsObject = new JWSObject(new JWSHeader(JWSAlgorithm.HS256),
                    payload);

            jwsObject.sign(new MACSigner(Constants.TOKEN_SECRET_KEY));
            return jwsObject.serialize();
        }
        catch (JOSEException e) {
            throw new RuntimeException("Error to create JWT", e);
        }
    }

}
