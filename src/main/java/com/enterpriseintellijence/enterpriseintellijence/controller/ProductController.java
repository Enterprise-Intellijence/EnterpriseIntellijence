package com.enterpriseintellijence.enterpriseintellijence.controller;

import com.enterpriseintellijence.enterpriseintellijence.data.services.ProductService;
import com.enterpriseintellijence.enterpriseintellijence.dto.basics.ProductBasicDTO;

import com.enterpriseintellijence.enterpriseintellijence.dto.ProductDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.basics.UserBasicDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.*;
import com.enterpriseintellijence.enterpriseintellijence.security.TokenStore;
import com.nimbusds.jose.JOSEException;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.persistence.EnumType;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.time.Duration;
import java.util.Arrays;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping(path="/api/v1/products", produces="application/json")
public class ProductController {
    private final ProductService productService;
    // TODO: 16/05/23 Erne

    private final Bandwidth limit = Bandwidth.classic(20, Refill.greedy(25, Duration.ofMinutes(1)));
    private final Bucket bucket = Bucket.builder().addLimit(limit).build();

    @PostMapping(consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public ProductDTO createProduct(@RequestBody @Valid ProductDTO productDTO){
        productDTO.setLikesNumber(0);
        productDTO.setViews(0);
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
        return ResponseEntity.ok(productService.getProductById(id, false));
    }

    @GetMapping("")
    public ResponseEntity<Page<ProductBasicDTO>> allProductPaged(@RequestParam int page, @RequestParam int size) {
        if (bucket.tryConsume(1)) {
            return ResponseEntity.ok(productService.getAllPaged(page, size));
        }
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
    }

    @GetMapping("/wardrobe")
    public ResponseEntity<Page<ProductBasicDTO>> getAllPagedBySellerId(@RequestBody UserBasicDTO userBasicDTO, @RequestParam int page, @RequestParam int size){
        
        if (bucket.tryConsume(1)) {
            return ResponseEntity.ok(productService.getAllPagedBySellerId(userBasicDTO,page,size));
        }

        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
    }

    @GetMapping("/category")
    public ResponseEntity<Page<ProductBasicDTO>> getProductFilteredForCategoriesPaged(@RequestParam("page") int page, @RequestParam("size") int size, @RequestParam("category") ProductCategory category){

        /*
        if (bucket.tryConsume(1)) {
*/

            return ResponseEntity.ok(productService.getProductFilteredForCategoriesPaged(page,size,category));
/*        }

        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();*/
    }

    @GetMapping("/category/clothing")
    public ResponseEntity<Page<ProductBasicDTO>> getClothingByTypePaged(@RequestParam("page") int page, @RequestParam("size") int size, @RequestParam("clothingType")ClothingType clothingType){

        /*
        if (bucket.tryConsume(1)) {
*/

        return ResponseEntity.ok(productService.getClothingByTypePaged(page,size,clothingType));
/*        }

        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();*/
    }
    @GetMapping("/category/entertainment")
    public ResponseEntity<Page<ProductBasicDTO>> getEntertainmentByTypePaged(@RequestParam("page") int page, @RequestParam("size") int size, @RequestParam("entertainmentType")EntertainmentType entertainmentType){

        /*
        if (bucket.tryConsume(1)) {
*/

        return ResponseEntity.ok(productService.getEntertainmentByTypePaged(page,size,entertainmentType));
/*        }

        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();*/
    }

    @GetMapping("/category/home")
    public ResponseEntity<Page<ProductBasicDTO>> getHomeByTypePaged(@RequestParam("page") int page, @RequestParam("size") int size, @RequestParam("homeType")HomeType homeType){

        /*
        if (bucket.tryConsume(1)) {
*/

        return ResponseEntity.ok(productService.getHomeByTypePaged(page,size,homeType));
/*        }

        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();*/
    }

    @GetMapping("/search")
    public ResponseEntity<Page<ProductBasicDTO>> searchProductByTitleOrDescription(@RequestParam("keystring") String keystring,@RequestParam("page") int page, @RequestParam("size") int size){
        return ResponseEntity.ok(productService.searchProduct(keystring,page,size));
    }

    @GetMapping("/search-by-price")
    public ResponseEntity<Page<ProductBasicDTO>> searchProductByPrice(@RequestParam("startPrice") Double startPrice,@RequestParam("endPrice") Double endPrice,@RequestParam("page") int page, @RequestParam("size") int size){
        return ResponseEntity.ok(productService.searchProductByPrice(startPrice,endPrice,page,size));
    }

    @GetMapping("/most-liked")
    public ResponseEntity<Page<ProductBasicDTO>> mostLikedProducts(@RequestParam("page") int page, @RequestParam("size") int size){
        return ResponseEntity.ok(productService.getMostLikedProducts(page,size));
    }

    @GetMapping("/most-viewed")
    public ResponseEntity<Page<ProductBasicDTO>> mostViewedProducts(@RequestParam("page") int page, @RequestParam("size") int size){
        return ResponseEntity.ok(productService.getMostViewedProducts(page,size));
    }





/*

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
    }*/

    //restituisce la lista dei sessi per l'abbigliamento
    @GetMapping("/categories/clothing/gender")
    public ResponseEntity<Iterable<ProductGender>> getProductGender(){
        return ResponseEntity.ok(Arrays.asList(ProductGender.class.getEnumConstants())) ;
    }

    @GetMapping("/capability/url/{id}")
    public ResponseEntity<String> getCapabilityUrl(@PathVariable("id") String id){
        return ResponseEntity.ok(productService.getCapabilityUrl(id));
    }

    @GetMapping("/likes/users/{id}")
    public ResponseEntity<Iterable<UserBasicDTO>> getProductLikedByUser(@PathVariable("id") String id, @RequestParam int page, @RequestParam int size){
        return ResponseEntity.ok(productService.getUserThatLikedProduct(id, page, size));
    }

    @GetMapping("/capability/{token}")
    public ResponseEntity<ProductDTO> getCapability(@PathVariable("token") String token) throws ParseException, JOSEException {
        return ResponseEntity.ok(productService.getProductById(TokenStore.getInstance().getIdByCapability(token), true));
    }
}
