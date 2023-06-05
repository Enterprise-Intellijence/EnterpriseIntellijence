package com.enterpriseintellijence.enterpriseintellijence.controller;

import com.enterpriseintellijence.enterpriseintellijence.data.services.FollowingService;
import com.enterpriseintellijence.enterpriseintellijence.dto.AddressDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.DeliveryDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.FollowingDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.creation.AddressCreateDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.creation.DeliveryCreateDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1", produces = "application/json")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class FollowingController {

    private final FollowingService followingService;

    @PostMapping("/following/set/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<FollowingDTO> follow(
            @PathVariable("id") String id)
    {
        return ResponseEntity.ok(followingService.follow(id));
    }

    @DeleteMapping(path = "/following/set/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unfollow(
            @PathVariable("id") String id)
    {
        followingService.unfollow(id);
    }

    @GetMapping("/{userId}/following")
    public ResponseEntity<Page<FollowingDTO>> getFollowingByUser(
            @PathVariable("userId") String id,
            @RequestParam(defaultValue = "0",required = false) int page,
            @RequestParam(defaultValue = "10",required = false) int sizePage)
    {
        return ResponseEntity.ok(followingService.getDeliveryById(id,page,sizePage));
    }

    @GetMapping("/{userId}/follower")
    public ResponseEntity<Page<FollowingDTO>> getFollowingByUser(
            @PathVariable("userId") String id,
            @RequestParam(defaultValue = "0",required = false) int page,
            @RequestParam(defaultValue = "10",required = false) int sizePage)
    {
        return ResponseEntity.ok(followingService.getDeliveryById(id,page,sizePage));
    }

}
