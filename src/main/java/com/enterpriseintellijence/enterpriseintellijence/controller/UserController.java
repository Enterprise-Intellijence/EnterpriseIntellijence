package com.enterpriseintellijence.enterpriseintellijence.controller;

import com.enterpriseintellijence.enterpriseintellijence.dto.AddressDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.UserDTO;
import com.enterpriseintellijence.enterpriseintellijence.data.services.UserService;
import com.enterpriseintellijence.enterpriseintellijence.dto.basics.*;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.Provider;
import com.enterpriseintellijence.enterpriseintellijence.security.LoginWithGoogleBody;
import com.enterpriseintellijence.enterpriseintellijence.security.Oauth2GoogleValidation;

import com.nimbusds.jose.Header;
import com.nimbusds.jose.JOSEException;
import jakarta.mail.MessagingException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jdk.jfr.ContentType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.lang.reflect.Array;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import static com.enterpriseintellijence.enterpriseintellijence.security.AppSecurityConfig.SECURITY_CONFIG_NAME;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@RestController
@RequiredArgsConstructor
@RequestMapping(path="/api/v1/users", produces="application/json")
@CrossOrigin(origins= "http://localhost:4200")
@Slf4j
@SecurityRequirement(name = SECURITY_CONFIG_NAME)
public class UserController {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final Oauth2GoogleValidation oauth2GoogleValidation;
  
    @PostMapping(path = "/authenticate" )
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Map<String, String>> authenticate( @RequestParam( "username" ) String username, @RequestParam( "password" ) String password, HttpServletResponse
            response) throws JOSEException {
        return ResponseEntity.ok(userService.authenticateUser(username, password, Provider.LOCAL));
    }

    @PostMapping(path= "/register" )
    @ResponseStatus(HttpStatus.OK)
    public void register( @RequestParam( "username" ) String username, @RequestParam("email") String email, @RequestParam( "password" ) String password) throws MessagingException {
        userService.registerUser(username, email, password);
        userService.sendVerificationEmail(username);
    }

    @GetMapping("/activate")
    @ResponseStatus(HttpStatus.OK)
    public void activate(@RequestParam("token") String unique_code) throws ParseException, JOSEException {
        userService.activateUser(unique_code);
    }


/*    @PostMapping(consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody UserDTO userDTO){
        return ResponseEntity.ok(userService.createUser(userDTO));

    }*/

//    @PutMapping(path = "/{id}",consumes="application/json")
//    public ResponseEntity<UserDTO> replaceUser(@PathVariable("id") String id, @Valid @RequestBody UserDTO userDTO) throws IllegalAccessException {
//        return ResponseEntity.ok(userService.replaceUser(id, userDTO));
//    }

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

    @GetMapping("/find-by-username")
    public ResponseEntity<UserBasicDTO> findByUsername(@RequestParam("username") String username){
        Optional<UserBasicDTO> userBasicDTO = userService.findBasicByUsername(username);
        return userBasicDTO.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.ok(null));
    }

    @GetMapping("/search-by-username")
    public ResponseEntity<Page<UserBasicDTO>> searchByUsername(@RequestParam("username") String username, @RequestParam("page") int page, @RequestParam("size") int size){
        return ResponseEntity.ok(userService.searchUsersByUsername(username, page, size));
    }


    @PostMapping("/google-auth")
    public ResponseEntity<Map<String, String>> googleAuth(@RequestParam String idTokenString) throws Exception {
        return ResponseEntity.ok(userService.googleAuth(idTokenString));
    }

    @PostMapping(value = "/login-with-google", consumes = "application/x-www-form-urlencoded;charset=UTF-8")
    public ResponseEntity<Map<String, String>> loginWithGoogle(LoginWithGoogleBody body) throws Exception {
        return ResponseEntity.ok(userService.googleAuth(body.getCredential()));
    }




    @GetMapping("/me")
    public ResponseEntity<UserDTO> me() throws EntityNotFoundException {
        return ResponseEntity.ok(userService.findMyProfile());
    }

    @GetMapping("/refreshToken")
    public ResponseEntity<Map<String, String >> refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        return ResponseEntity.ok(userService.refreshToken(request.getHeader(AUTHORIZATION), response));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) throws IOException, ParseException, JOSEException {
        userService.logout(request.getHeader(AUTHORIZATION));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/changePassword")
    public ResponseEntity<Void> changePassword(@RequestParam("oldPassword") String oldPassword, @RequestParam("newPassword") String newPassword, HttpServletRequest request) throws EntityNotFoundException, MessagingException, ParseException, JOSEException {
        userService.changePassword(oldPassword, newPassword, request.getHeader(AUTHORIZATION));
        return ResponseEntity.ok().build();
    }

    @GetMapping("/resetPassword")
    public ResponseEntity<Void> resetPassword(@RequestParam("email") String email) throws EntityNotFoundException, MessagingException {
        userService.resetPassword(email);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/getNewPassword")
    public ResponseEntity<Void> resetPasswordToken(@RequestParam("token") String token, HttpServletRequest request) throws EntityNotFoundException, ParseException, JOSEException, MessagingException {
        userService.changePassword(token, request.getHeader(AUTHORIZATION));
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

    @GetMapping("/me/orders")
    public ResponseEntity<Page<OrderBasicDTO>> getMyOrders(@RequestParam int page, @RequestParam int size) throws EntityNotFoundException {
        return ResponseEntity.ok(userService.getMyOrders(page, size));
    }


    @GetMapping("/me/offers")
    public ResponseEntity<Page<OfferBasicDTO>> getMyOffers(@RequestParam int page, @RequestParam int size) throws EntityNotFoundException {
        return ResponseEntity.ok(userService.getMyOffers(page, size));
    }

    @GetMapping("/default-address")
    public ResponseEntity<AddressDTO> getDefaultAddress(@RequestParam String userId) throws EntityNotFoundException {
        return ResponseEntity.ok(userService.getDefaultAddress(userId));
    }


}
