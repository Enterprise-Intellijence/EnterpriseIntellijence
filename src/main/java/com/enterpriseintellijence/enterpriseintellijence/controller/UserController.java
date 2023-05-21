package com.enterpriseintellijence.enterpriseintellijence.controller;

import com.enterpriseintellijence.enterpriseintellijence.dto.MessageDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.PaymentMethodDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.UserDTO;
import com.enterpriseintellijence.enterpriseintellijence.data.services.UserService;
import com.enterpriseintellijence.enterpriseintellijence.dto.basics.OrderBasicDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.basics.PaymentMethodBasicDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.basics.ProductBasicDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.basics.UserBasicDTO;
import com.enterpriseintellijence.enterpriseintellijence.security.TokenStore;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JOSEException;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.text.ParseException;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

@RestController
@RequiredArgsConstructor
@RequestMapping(path="/api/v1/users", produces="application/json")
@CrossOrigin(origins="*")
@Slf4j
public class UserController {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    // TODO: 16/05/23 Ciccio


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
  
    @PostMapping(path = "/authenticate" )
    @ResponseStatus(HttpStatus.OK)
    public void authenticate( @RequestParam( "username" ) String username, @RequestParam( "password" ) String password, HttpServletResponse
            response) throws JOSEException {
        Map<String, String> tokens = userService.authenticateUser(username, password);
        response.addHeader(AUTHORIZATION, "Bearer " + tokens.get("accessToken"));
        response.addHeader("RefreshToken", "Bearer " + tokens.get("refreshToken"));
    }

    @PostMapping(path= "/register" )
    public ResponseEntity<String> register( @RequestParam( "username" ) String username, @RequestParam("email") String email, @RequestParam( "password" ) String password) {
        return userService.registerUser(username, email, password);
    }


/*    @PostMapping(consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody UserDTO userDTO){
        return ResponseEntity.ok(userService.createUser(userDTO));

    }*/

    @PutMapping(path = "/{id}",consumes="application/json")
    public ResponseEntity<UserDTO> replaceUser(@PathVariable("id") String id, @Valid @RequestBody UserDTO userDTO) throws IllegalAccessException {
        return ResponseEntity.ok(userService.replaceUser(id, userDTO));
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

    @GetMapping("/{id}")
    public ResponseEntity<UserBasicDTO> userById(@PathVariable("id") String id){
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

/*    @GetMapping("/{username}" )
    @PreAuthorize( "#username.equals(authentication.name)")
    public ResponseEntity<Optional<UserDTO>> getUser(@PathVariable( "username" ) String username) {
        var user = userService.findByUsername(username);
        return ResponseEntity.ok(user);
    }*/

    @GetMapping("/me")
    public ResponseEntity<UserDTO> me() throws EntityNotFoundException {
        UserDTO userDTO = userService.findUserFromContext().orElseThrow(EntityNotFoundException::new);
        return ResponseEntity.ok(userDTO);
    }

    @GetMapping("/refreshToken")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        userService.refreshToken(request.getHeader(AUTHORIZATION), response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) throws IOException, ParseException, JOSEException {
        userService.logout(request.getHeader(AUTHORIZATION));
        return ResponseEntity.ok().build();
    }

    @GetMapping("/followers/{id}")
    public ResponseEntity<Page<UserBasicDTO>> getFollowers(@PathVariable("id") String id, @RequestParam int page, @RequestParam int size) throws EntityNotFoundException {
        if (userService.findUserById(id) == null)
            return ResponseEntity.notFound().build();

        return ResponseEntity.ok(userService.getFollowersByUserId(id, page, size));
    }

    @GetMapping("/following/{id}")
    public ResponseEntity<Page<UserBasicDTO>> getFollowing(@PathVariable("id") String id, @RequestParam int page, @RequestParam int size){
        if (userService.findUserById(id) == null)
            return ResponseEntity.notFound().build();

        return ResponseEntity.ok(userService.getFollowingByUserId(id, page, size));
    }

    @PostMapping("/follow/{id}")
    public ResponseEntity<Void> follow(@PathVariable("id") String id) throws EntityNotFoundException {
        userService.followUser(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/unfollow/{id}")
    public ResponseEntity<Void> unfollow(@PathVariable("id") String id) throws EntityNotFoundException {
        userService.unfollowUser(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/like/{id}")
    public ResponseEntity<Void> like(@PathVariable("id") String id) throws EntityNotFoundException {
        userService.addLikeToProduct(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/unlike/{id}")
    public ResponseEntity<Void> unlike(@PathVariable("id") String id) throws EntityNotFoundException {
        userService.removeLikeFromProduct(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/liked/")
    public ResponseEntity<Page<ProductBasicDTO>> getLikedProducts(@RequestParam int page, @RequestParam int size) throws EntityNotFoundException {
        return ResponseEntity.ok(userService.getProductLikedByUser(page, size));
    }

    @GetMapping("/my/orders")
    public ResponseEntity<Page<OrderBasicDTO>> getMyOrders(@RequestParam int page, @RequestParam int size) throws EntityNotFoundException {
        return ResponseEntity.ok(userService.getMyOrders(page, size));
    }

    @GetMapping("/my/payment-methods")
    public ResponseEntity<Page<PaymentMethodBasicDTO>> getMyPaymentMethods(@RequestParam int page, @RequestParam int size) throws EntityNotFoundException {
        return ResponseEntity.ok(userService.getMyPaymentMethods(page, size));
    }

    @GetMapping("/my/inbox")
    public ResponseEntity<Page<MessageDTO>> getMyInBoxMessage(@RequestParam int page, @RequestParam int size) throws EntityNotFoundException {
        return ResponseEntity.ok(userService.getMyInBoxMessage(page, size));
    }

    @GetMapping("/my/outbox")
    public ResponseEntity<Page<MessageDTO>> getMyOutBoxMessage(@RequestParam int page, @RequestParam int size) throws EntityNotFoundException {
        return ResponseEntity.ok(userService.getMyOutBoxMessage(page, size));
    }


}
