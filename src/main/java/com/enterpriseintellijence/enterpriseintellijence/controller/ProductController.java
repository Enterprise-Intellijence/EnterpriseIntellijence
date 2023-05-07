package com.enterpriseintellijence.enterpriseintellijence.controller;

import com.enterpriseintellijence.enterpriseintellijence.data.services.ProductService;
import com.enterpriseintellijence.enterpriseintellijence.dto.ProductDTO;

import com.enterpriseintellijence.enterpriseintellijence.dto.enums.*;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.Arrays;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping(path="/api/v1/products", produces="application/json")
public class ProductController {
    private final ProductService productService;

    private final Bandwidth limit = Bandwidth.classic(20, Refill.greedy(25, Duration.ofMinutes(1)));
    private final Bucket bucket = Bucket.builder().addLimit(limit).build();

    @PostMapping(consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public ProductDTO createProduct(@RequestBody @Valid ProductDTO productDTO){
        return productService.createProduct(productDTO);
    }

    @PutMapping(path = "/{id}",consumes="application/json")
    public ResponseEntity<ProductDTO> replaceProduct(@PathVariable("id") String id, @Valid @RequestBody ProductDTO productDTO){
        return ResponseEntity.ok(productService.replaceProduct(id, productDTO));
    }

    @PatchMapping(path="/{id}", consumes = "application/json")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable("id") String id, @Valid @RequestBody ProductDTO patch) {
        return ResponseEntity.ok(productService.updateProduct(id, patch));
    }

    @DeleteMapping(path="/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteProduct(@PathVariable("id") String id){
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> productById(@PathVariable("id") String id){
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @GetMapping("")
    public ResponseEntity<Iterable<ProductDTO>> allProduct() {
        return ResponseEntity.ok(productService.findAll());
    }

    @GetMapping("/paged")
    public ResponseEntity<Page<ProductDTO>> getAllPaged(@RequestParam int page, @RequestParam int size){
        
        if (bucket.tryConsume(1)) {
            return ResponseEntity.ok(productService.getAllPaged(page,size));
        }

        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
    }

    @GetMapping("/paged")
    public ResponseEntity<Page<ProductDTO>> getProductFilteredForCategoriesPaged(@RequestParam int page, @RequestParam int size, @RequestParam ProductCategory productCategory){

        if (bucket.tryConsume(1)) {
            return ResponseEntity.ok(productService.getProductFilteredForCategoriesPaged(page,size,productCategory));
        }

        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
    }

    //restituisce la lista di categorie
    @GetMapping("/categories")
    public ResponseEntity<Iterable<ProductCategory>> getCategoriesList(){
        return ResponseEntity.ok(Arrays.asList(ProductCategory.class.getEnumConstants())) ;
    }

    //restituisce la sottolista della categoria home
    @GetMapping("/categories/home")
    public ResponseEntity<Iterable<HomeType>> getHomeType(){
        return ResponseEntity.ok(Arrays.asList(HomeType.class.getEnumConstants())) ;
    }

    //restituisce la sottolista della categoria entertainment
    @GetMapping("/categories/entertainment")
    public ResponseEntity<Iterable<EntertainmentType>> getEntertainmentType(){
        return ResponseEntity.ok(Arrays.asList(EntertainmentType.class.getEnumConstants())) ;
    }

    //restituisce la sottolista della categoria clothing
    @GetMapping("/categories/clothing")
    public ResponseEntity<Iterable<ClothingType>> getClothingType(){
        return ResponseEntity.ok(Arrays.asList(ClothingType.class.getEnumConstants())) ;
    }

    //restituisce la lista di colori
    @GetMapping("/colour")
    public ResponseEntity<Iterable<Colour>> getColour(){
        return ResponseEntity.ok(Arrays.asList(Colour.class.getEnumConstants())) ;
    }

    //restituisce la lista delle misure per la categoria clothing
    @GetMapping("/categories/clothing/size")
    public ResponseEntity<Iterable<ClothingSize>> getClothingSize(){
        return ResponseEntity.ok(Arrays.asList(ClothingSize.class.getEnumConstants())) ;
    }

    //restituisce la lista dei sessi per l'abbigliamento
    @GetMapping("/categories/clothing/gender")
    public ResponseEntity<Iterable<ProductGender>> getProductGender(){
        return ResponseEntity.ok(Arrays.asList(ProductGender.class.getEnumConstants())) ;
    }


}
