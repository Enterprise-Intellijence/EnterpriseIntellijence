package com.enterpriseintellijence.enterpriseintellijence.controller;

import com.enterpriseintellijence.enterpriseintellijence.data.services.ImageService;
import com.enterpriseintellijence.enterpriseintellijence.dto.ProductImageDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.UserImageDTO;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static com.enterpriseintellijence.enterpriseintellijence.security.AppSecurityConfig.SECURITY_CONFIG_NAME;


@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/images", produces = "application/json")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@SecurityRequirement(name = SECURITY_CONFIG_NAME)
public class ImageController {
    private final ImageService imageService;

    @GetMapping(path = "/{type}/{folder_name}/{file_name:.*}", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<Resource> getImage(@PathVariable("type" )String type, @PathVariable("folder_name")String folder_name ,@PathVariable("file_name") String file_name) throws IOException {

        Resource resource = imageService.getImage(type+"/"+folder_name+"/"+file_name);
        return ResponseEntity.ok(resource);
    }

    @PostMapping("/users/photo-profile")
    @ResponseStatus(HttpStatus.CREATED)
    public UserImageDTO savePhotoUser(@RequestBody MultipartFile multipartFile, @RequestParam("description") String description) throws IOException {
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


    @PostMapping(value =  "/product")
    @ResponseStatus(HttpStatus.CREATED)
    public ProductImageDTO saveImageProduct(@RequestBody MultipartFile multipartFile, @RequestParam("product_id") String product_id, @RequestParam("description") String description) throws IOException, IllegalAccessException {
        return imageService.saveImageProduct(multipartFile,product_id,description);
    }

    @PutMapping("/product/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void replaceImageProduct(@PathVariable("id")String id,@RequestBody MultipartFile multipartFile) throws IOException {
        imageService.replaceImageProduct(id,multipartFile);
    }

    @DeleteMapping("/product/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteImageProduct(@PathVariable("id") String id) throws IllegalAccessException {
        imageService.deleteImageProduct(id);
    }

/*    @GetMapping(value = "/product/{folder_name}/{file_name:.*}", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<Resource> getImageProduct(@PathVariable("folder_name")String folder_name ,@PathVariable("file_name") String file_name) {
        return ResponseEntity.ok(imageService.getImageProduct(folder_name+"/"+file_name));
    }*/




}
