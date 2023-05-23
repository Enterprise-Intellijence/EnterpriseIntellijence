package com.enterpriseintellijence.enterpriseintellijence.controller;

import com.enterpriseintellijence.enterpriseintellijence.data.services.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/images", produces = "application/json")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ImageController {
    private final ImageService imageService;

    @GetMapping(path = "/get/photo-profile/{id}")
    public ResponseEntity<byte[]> getUserProfilePhoto(@PathVariable("id") String id) {
        return ResponseEntity.ok(imageService.getUserProfilePhoto(id));
    }

    @GetMapping("/get/product-default-image/{id}")
    public ResponseEntity<byte[]> getProductPhotoForCard(@PathVariable("id") String id) {
        return ResponseEntity.ok(imageService.getProductCardPhoto(id));
    }

}
