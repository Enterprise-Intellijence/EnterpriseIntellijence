package com.enterpriseintellijence.enterpriseintellijence.controller;

import com.enterpriseintellijence.enterpriseintellijence.dto.PaymentMethodDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.UserDTO;
import com.enterpriseintellijence.enterpriseintellijence.data.services.UserService;
import com.enterpriseintellijence.enterpriseintellijence.security.TokenStore;

import com.nimbusds.jose.JOSEException;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping(path="/api/v1/users", produces="application/json")
@CrossOrigin(origins="*")
@Slf4j
public class UserController {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;


    private final Bandwidth limit = Bandwidth.classic(20, Refill.greedy(25, Duration.ofMinutes(1)));
    private final Bucket bucket = Bucket.builder().addLimit(limit).build();


    //ESEMPIO DI RATE LIMITING
    /*

    @GetMapping("/exampleRateLimit")
    public ResponseEntity<String> exampleRateLimit() {
        if (bucket.tryConsume(1))
            return ResponseEntity.ok("Hello World!");
        else
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(null);
     }

     Guarda il metodo sotto per un esempio pratico
     */
    @GetMapping("")
    public ResponseEntity<Iterable<UserDTO>> allUser() {
        if (bucket.tryConsume(1))
            return ResponseEntity.ok(userService.findAll());
        else
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(null);
    }

    @PostMapping(consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody UserDTO userDTO){
        return ResponseEntity.ok(userService.createUser(userDTO));

    }

    @PutMapping(path = "/{id}",consumes="application/json")
    public ResponseEntity<UserDTO> replaceUser(@PathVariable("id") String id, @Valid @RequestBody UserDTO userDTO) throws IllegalAccessException {
        return ResponseEntity.ok(userService.replaceUser(id,userDTO));
    }

    @PatchMapping(path="/{id}", consumes = "application/json")
    public ResponseEntity<UserDTO> updateUser(@PathVariable("id") String id, @Valid @RequestBody UserDTO patch) throws IllegalAccessException {
            return ResponseEntity.ok(userService.updateUser(id,patch));
    }

    @DeleteMapping(path="/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteUser(@PathVariable("id") String id){
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    // TODO: 07/05/2023 occhio ci sono due metodi che si mappano allo stesso modo, riga 93 e riga 115 
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> userById(@PathVariable("id") String id){
        return ResponseEntity.ok(userService.findUserById(id));
    }

/*
    @GetMapping("/google_auth")
    public ResponseEntity<UserDTO> googleAuth(Model model, @RegisteredOAuth2AuthorizedClient OAuth2AuthorizedClient authorizedClient, @AuthenticationPrincipal OAuth2User oauth2User) {
         model.addAttribute( "userName" , oauth2User.getName());
        model.addAttribute( "clientName" , authorizedClient.getClientRegistration().getClientName());
        model.addAttribute( "userAttributes" , oauth2User.getAttributes());
       UserDTO user = userService.findByUsername(oauth2User.getName());
        if(user == null) {
            user = new UserDTO();
            user.setEmail(oauth2User.getAttributes().get( "email" ).toString());
            user.setUsername(oauth2User.getName());

            userService.createUser(user);
        }
        return new ResponseEntity<>(user, HttpStatus.OK);
    }*/

    @GetMapping("/{username}" )
    @PreAuthorize( "#username.equals(authentication.name)")
    public ResponseEntity<Optional<UserDTO>> getUser(@PathVariable( "username" ) String username) {
        var user = userService.findByUsername(username);
        return ResponseEntity.ok(user);
    }

    @PostMapping(path = "/authenticate" )
    @ResponseStatus(HttpStatus.OK)
    public void authenticate( @RequestParam( "username" ) String username, @RequestParam( "password" ) String password, HttpServletResponse
            response) throws JOSEException {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        String token = TokenStore.getInstance().createToken(Map.of("username", username));
        response.addHeader(HttpHeaders.AUTHORIZATION,
                "Bearer " + token);
    }

    @PostMapping(path= "/register" )
    public ResponseEntity<String> register( @RequestParam( "username" ) String username, @RequestParam("email") String email, @RequestParam( "password" ) String password) {
        if(userService.findByUsername(username).isPresent())
            return new ResponseEntity<>( "existing username" , HttpStatus.CONFLICT);
        UserDTO user = new UserDTO();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setEmail(email);

        userService.createUser(user);
        log.info("User created: " + user.toString());
        return new ResponseEntity<>( "registered" , HttpStatus.OK);
    }

    @GetMapping("/me")
    public ResponseEntity<UserDTO> me() throws EntityNotFoundException {
        UserDTO userDTO = userService.findUserFromContext().orElseThrow(EntityNotFoundException::new);
        return ResponseEntity.ok(userDTO);
    }

    @GetMapping("/me/payment_methods")
    public ResponseEntity<Page<PaymentMethodDTO>> getPaymentMethods(@RequestParam Pageable page) throws EntityNotFoundException {
        if (bucket.tryConsume(1)) {
            UserDTO userDTO = userService.findUserFromContext().orElseThrow(EntityNotFoundException::new);
            return ResponseEntity.ok(userService.getPaymentMethodsByUserId(userDTO, page));
        } else {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(null);
        }
    }
}
