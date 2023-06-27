package com.enterpriseintellijence.enterpriseintellijence.security;

import com.enterpriseintellijence.enterpriseintellijence.data.services.UserService;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.LowLevelHttpRequest;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
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



    private final GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), GsonFactory.getDefaultInstance())
                .setAudience(Collections.singleton(clientId))
                .build();

    public Map<String, String> validate(String idTokenString) throws Exception {

        System.out.println("idTokenString: " + idTokenString);
        System.out.println("clientId: " + clientId);


        GoogleIdToken idToken = verifier.verify(idTokenString);

        System.out.println("idToken: " + idToken);

        if (idToken != null) {
            GoogleIdToken.Payload payload = idToken.getPayload();

            String userId = payload.getSubject();
            System.out.println("User ID: " + userId);

            String email = payload.getEmail();
            boolean emailVerified = payload.getEmailVerified();
            String name = (String) payload.get("name");
            String pictureUrl = (String) payload.get("picture");
            String locale = (String) payload.get("locale");
            String familyName = (String) payload.get("family_name");
            String givenName = (String) payload.get("given_name");

            System.out.println("Email: " + email);
            System.out.println("Email verified: " + emailVerified);
            System.out.println("Name: " + name);
            System.out.println("Picture URL: " + pictureUrl);
            System.out.println("Locale: " + locale);
            System.out.println("Family Name: " + familyName);
            System.out.println("Given Name: " + givenName);

            return Map.of("email", email, "name", name, "pictureUrl", pictureUrl, "locale", locale, "familyName", familyName, "givenName", givenName);


        } else {
            System.out.println("Invalid ID token.");
            throw new Exception("Invalid ID token.");
        }
    }
}
