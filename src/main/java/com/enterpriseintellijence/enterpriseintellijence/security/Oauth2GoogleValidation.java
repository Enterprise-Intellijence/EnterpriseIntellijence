package com.enterpriseintellijence.enterpriseintellijence.security;

import com.enterpriseintellijence.enterpriseintellijence.data.services.UserService;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.LowLevelHttpRequest;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.Clock;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;


@Component
public class Oauth2GoogleValidation {


    @Value("${spring.security.oauth2.client.registration.google.clientId}")
    private String clientId;



    private GoogleIdTokenVerifier verifier;

    public Map<String, String> validate(String idTokenString) throws Exception {
        if(verifier == null) {
            verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), GsonFactory.getDefaultInstance())
                .setAudience(Collections.singleton(clientId))
                .setIssuer("https://accounts.google.com")
                .setClock(Clock.SYSTEM)
                .build();
        }

        GoogleIdToken idToken = verifier.verify(idTokenString);


        if (idToken != null) {
            GoogleIdToken.Payload payload = idToken.getPayload();

            System.out.println(payload);

            String userId = payload.getSubject();
            String email = payload.getEmail();
            boolean emailVerified = payload.getEmailVerified();
            String name = (String) payload.get("name");
            String pictureUrl = (String) payload.get("picture");
            String familyName = (String) payload.get("family_name");
            String givenName = (String) payload.get("given_name");


            return Map.of("email", email, "name", name, "pictureUrl", pictureUrl, "familyName", familyName, "givenName", givenName);


        } else {
            System.out.println("Invalid ID token.");
            throw new Exception("Invalid ID token.");
        }
    }
}
