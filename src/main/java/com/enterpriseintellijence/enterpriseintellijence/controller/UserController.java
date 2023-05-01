package com.enterpriseintellijence.enterpriseintellijence.controller;

import com.enterpriseintellijence.enterpriseintellijence.dto.UserDTO;
import com.enterpriseintellijence.enterpriseintellijence.data.services.UserService;
import com.enterpriseintellijence.enterpriseintellijence.security.TokenStore;

import com.nimbusds.jose.JOSEException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping(path="/api/v1/users", produces="application/json")
@CrossOrigin(origins="*")
public class UserController {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    @PostMapping(consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDTO createUser(@Valid @RequestBody UserDTO userDTO){
        return userService.createUser(userDTO);
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
    public void deleteUser(@PathVariable("id") String id){
        userService.deleteUser(id);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> userById(@PathVariable("id") String id){
        return ResponseEntity.ok(userService.findUserById(id));
    }

    @GetMapping("")
    public Iterable<UserDTO> allUser() {
        return userService.findAll();
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
        System.out.println("ciao");

        userService.createUser(user);
        return new ResponseEntity<>( "registered" , HttpStatus.OK);
    }
}
