package com.enterpriseintellijence.enterpriseintellijence.controller;

import com.enterpriseintellijence.enterpriseintellijence.data.services.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/images", produces = "application/json")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ImageController {
    private final ImageService imageService;

    @PostMapping("/users/photo-profile")
    @ResponseStatus(HttpStatus.CREATED)
    public String savePhotoUser(@RequestBody MultipartFile multipartFile,@RequestParam("description") String description) throws IOException {
        return imageService.savePhotoUser(multipartFile,description);
    }

    @PutMapping("/users/photo-profile/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void replacePhotoUser(@PathVariable("id")String id,@RequestBody MultipartFile multipartFile) throws IOException {
        imageService.replacePhotoUser(id,multipartFile);
    }

    @DeleteMapping("/users/photo-profile/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePhotoUser(@PathVariable("id") String id) throws IllegalAccessException {
        imageService.deletePhotoUser(id);
    }

    @GetMapping(path = "/users/photo-profile/{id}")
    public ResponseEntity<byte[]> getUserProfilePhoto(@PathVariable("id") String id) throws IOException {
        return ResponseEntity.ok(imageService.getUserProfilePhoto(id));
    }

    @PostMapping("/product/image")
    @ResponseStatus(HttpStatus.CREATED)
    public String saveImageProduct(@RequestBody MultipartFile multipartFile,@RequestParam("product_id")String product_id,@RequestParam("description") String description) throws IOException, IllegalAccessException {
        return imageService.saveImageProduct(multipartFile,product_id,description);
    }

    @PutMapping("/product/image/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void replaceImageProduct(@PathVariable("id")String id,@RequestBody MultipartFile multipartFile) throws IOException {
        imageService.replaceImageProduct(id,multipartFile);
    }

    @DeleteMapping("/product/image/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteImageProduct(@PathVariable("id") String id) throws IllegalAccessException {
        imageService.deleteImageProduct(id);
    }

    @GetMapping("/product/image/{id}")
    public ResponseEntity<byte[]> getImageProduct(@PathVariable("id") String id) {
        return ResponseEntity.ok(imageService.getImageProduct(id));
    }




}
