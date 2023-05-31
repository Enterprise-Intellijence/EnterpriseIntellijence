package com.enterpriseintellijence.enterpriseintellijence.security;

import com.enterpriseintellijence.enterpriseintellijence.data.entities.InvalidToken;
import com.enterpriseintellijence.enterpriseintellijence.data.repository.InvalidTokensRepository;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import javax.xml.crypto.Data;
import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TokenStore {

    private final InvalidTokensRepository invalidTokensRepository;
    private final JwtContextUtils jwtContextUtils;
    private final String secretKey = Constants.TOKEN_SECRET_KEY;

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

    public boolean verifyToken(String token, String claim) throws JOSEException, ParseException {
        try {
            jwtContextUtils.getUsernameFromContext().ifPresentOrElse(username -> {
                try {
                    if(!username.equals(getUser(token)) || invalidTokensRepository.findByToken(token) != null)
                        throw new RuntimeException("Invalid token");
                    if(claim != null && !claim.equals(getClaim(token)))
                        throw new RuntimeException("Invalid token");
                } catch (JOSEException | ParseException e) {
                    throw new RuntimeException(e);
                }
            }, () -> {
                throw new RuntimeException("Invalid token");
            });
            return true;
        } catch (RuntimeException e) {
            return false;
        }
    }

    public String getClaim(String token) {
        try {
            return SignedJWT.parse(token).getJWTClaimsSet().getClaim("claim").toString();
        } catch (ParseException e) {
            throw new RuntimeException(e);
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
        flushInvalidTokens();
        try {
            JWTClaimsSet claims = new JWTClaimsSet.Builder()
                    .claim("username", username)
                    .expirationTime(Date.from(Instant.now().plus(Constants.JWT_REFRESH_EXPIRATION_TIME, ChronoUnit.HOURS)))
                    .notBeforeTime(Date.from(Instant.now()))
                    .issueTime(Date.from(Instant.now()))
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

    public String createEmailToken(String username, String claim) {
        flushInvalidTokens();
        try {
            JWTClaimsSet claims = new JWTClaimsSet.Builder()
                    .claim("username", username)
                    .claim("claim", claim)
                    .expirationTime(Date.from(Instant.now().plus(Constants.EMAIL_VERIFICATION_TOKEN_EXPIRATION_TIME, ChronoUnit.HOURS)))
                    .notBeforeTime(Date.from(Instant.now()))
                    .issueTime(Date.from(Instant.now()))
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

    public String createCapabilityToken(String capability) {

        try {
            JWTClaimsSet claims = new JWTClaimsSet.Builder()
                    .claim("id-capability", capability)
                    .expirationTime(Date.from(Instant.now().plus(Constants.JWT_CAPABILITY_EXPIRATION_TIME, ChronoUnit.HOURS)))
                    .notBeforeTime(Date.from(Instant.now()))
                    .issueTime(Date.from(Instant.now()))
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

    public String getIdByCapability(String token) throws JOSEException, ParseException {
        SignedJWT signedJWT = SignedJWT.parse(token);
        JWSVerifier jwsVerifier = new MACVerifier(Constants.TOKEN_SECRET_KEY);
        if(signedJWT.verify(jwsVerifier)) {
            if(new Date().before(signedJWT.getJWTClaimsSet().getExpirationTime()) && new Date().after(signedJWT.getJWTClaimsSet().getNotBeforeTime()))
                return (String) signedJWT.getPayload().toJSONObject().get("id-capability");
        }
        throw new RuntimeException("Invalid token");
    }

    public void logout(String accessToken) throws ParseException, JOSEException {
        verifyToken(accessToken, null);
        InvalidToken invalidToken = new InvalidToken();
        invalidToken.setToken(accessToken);
        SignedJWT signedJWT = SignedJWT.parse(accessToken);
        invalidToken.setExpirationDate(signedJWT.getJWTClaimsSet().getExpirationTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().truncatedTo(ChronoUnit.SECONDS));
        invalidTokensRepository.save(invalidToken);
    }

    public void flushInvalidTokens() {
        LocalDateTime now = LocalDateTime.now();
        List<InvalidToken> invalidTokens = invalidTokensRepository.findAllByExpirationDateBefore(now);
        invalidTokensRepository.deleteAll(invalidTokens);
    }
}
