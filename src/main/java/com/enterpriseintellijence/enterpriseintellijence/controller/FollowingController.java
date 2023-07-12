package com.enterpriseintellijence.enterpriseintellijence.controller;

import com.enterpriseintellijence.enterpriseintellijence.data.services.FollowingService;
import com.enterpriseintellijence.enterpriseintellijence.dto.FollowingFollowersDTO;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.enterpriseintellijence.enterpriseintellijence.security.AppSecurityConfig.SECURITY_CONFIG_NAME;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1", produces = "application/json")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@SecurityRequirement(name = SECURITY_CONFIG_NAME)
public class FollowingController {

    private final FollowingService followingService;

    @PostMapping("/follow/{userId}")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<FollowingFollowersDTO> follow(
            @PathVariable("userId") String id) throws IllegalAccessException {
        return ResponseEntity.ok(followingService.follow(id));
    }

    @DeleteMapping(path = "/follow/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unfollow(
            @PathVariable("userId") String id) throws IllegalAccessException {
        followingService.unfollow(id);
    }

    @GetMapping("/following/{userId}")
    public ResponseEntity<Page<FollowingFollowersDTO>> getFollowingByUser(
            @PathVariable("userId") String id,
            @RequestParam(defaultValue = "0",required = false) int page,
            @RequestParam(defaultValue = "10",required = false) int sizePage)
    {
        return ResponseEntity.ok(followingService.getFollowingByUser(id,page,sizePage));
    }

    @GetMapping("/followers/{userId}")
    public ResponseEntity<Page<FollowingFollowersDTO>> getFollowersOfUser(
            @PathVariable("userId") String id,
            @RequestParam(defaultValue = "0",required = false) int page,
            @RequestParam(defaultValue = "10",required = false) int sizePage)
    {
        return ResponseEntity.ok(followingService.getFollowersOfUser(id,page,sizePage));
    }

    @GetMapping("/me/following/{userId}")
    public ResponseEntity<Boolean> imFollowingThisUser(@PathVariable(name = "userId")String userId){
        return followingService.imFollowingThisUser(userId);

    }

}
