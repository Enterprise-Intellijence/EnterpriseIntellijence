package com.enterpriseintellijence.enterpriseintellijence.controller;

import com.enterpriseintellijence.enterpriseintellijence.data.entities.Product;
import com.enterpriseintellijence.enterpriseintellijence.data.entities.User;
import com.enterpriseintellijence.enterpriseintellijence.data.repository.UserRepository;
import com.enterpriseintellijence.enterpriseintellijence.data.services.ProductService;
import com.enterpriseintellijence.enterpriseintellijence.data.specification.ProductSpecification;
import com.enterpriseintellijence.enterpriseintellijence.dto.MessageDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.basics.OfferBasicDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.basics.OrderBasicDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.basics.ProductBasicDTO;

import com.enterpriseintellijence.enterpriseintellijence.dto.ProductDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.basics.UserBasicDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.*;
import com.enterpriseintellijence.enterpriseintellijence.security.TokenStore;
import com.nimbusds.jose.JOSEException;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping(path="/api/v1/products", produces="application/json")
public class ProductController {
    private final ProductService productService;
    private final TokenStore tokenStore;
    private final UserRepository userRepository;
    // TODO: 16/05/23 Erne

    private final Bandwidth limit = Bandwidth.classic(20, Refill.greedy(25, Duration.ofMinutes(1)));
    private final Bucket bucket = Bucket.builder().addLimit(limit).build();

    @PostMapping(consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public ProductDTO createProduct(@RequestBody @Valid ProductDTO productDTO) throws IllegalAccessException {

        return productService.createProduct(productDTO);
    }

    @PutMapping(path = "/{id}",consumes="application/json")
    public ResponseEntity<ProductDTO> replaceProduct(@PathVariable("id") String id, @Valid @RequestBody ProductDTO productDTO){
        return ResponseEntity.ok(productService.replaceProduct(id, productDTO));
    }

    @PatchMapping(path="/{id}", consumes = "application/json")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable("id") String id, @Valid @RequestBody ProductDTO patch) throws IllegalAccessException {
        return ResponseEntity.ok(productService.updateProduct(id, patch));
    }

    @DeleteMapping(path="/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteProduct(@PathVariable("id") String id) throws IllegalAccessException {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> productById(@PathVariable("id") String id){
        return ResponseEntity.ok(productService.getProductById(id, false));
    }

/*    @GetMapping("")
    public ResponseEntity<Page<ProductBasicDTO>> allProductPaged(@RequestParam int page, @RequestParam int size) {
        if (bucket.tryConsume(1)) {
            return ResponseEntity.ok(productService.getAllPaged(page, size));
        }
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
    }*/

    @GetMapping("/filter")
    public ResponseEntity<Page<ProductBasicDTO>> getFilteredProducts(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) Double minProductCost,
            @RequestParam(required = false) Double maxProductCost,
            @RequestParam(required = false) String[] brands,
            @RequestParam(required = false) Condition condition,
            @RequestParam(required = false) Integer views,
            @RequestParam(required = false) ProductGender productGender,
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) LocalDateTime uploadDate,
            @RequestParam(required = false) Availability availability,
            @RequestParam(required = false) ProductCategory productCategory,
            @RequestParam(required = false) ProductCategoryParent productCategoryParent,
            @RequestParam(required = false) ProductCategoryChild productCategoryChild,
            @RequestParam(required = false) Integer likesNumber,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "uploadDate",required = false) String sortBy,
            @RequestParam(required = false, defaultValue = "DESC") String sortDirection
    ) {
        ProductSpecification.Filter filter = new ProductSpecification.Filter(userRepository );
        filter.setTitle(title);
        filter.setDescription(description);
        filter.setMinProductCost(minProductCost);
        filter.setMaxProductCost(maxProductCost);
        filter.setBrands(brands != null ? Arrays.asList(brands) : null);
        filter.setCondition(condition);
        filter.setViews(views);
        filter.setProductGender(productGender);
        filter.setSeller(userId);
        filter.setUploadDate(uploadDate);
        filter.setAvailability(availability);
        filter.setProductCategory(productCategory);
        filter.setProductCategoryParent(productCategoryParent);
        filter.setProductCategoryChild(productCategoryChild);
        filter.setLikesNumber(likesNumber);

        //Specification<Product> specification = ProductSpecification.withFilters(filter);

        if (bucket.tryConsume(1)) {
            return ResponseEntity.ok(productService.getProductFilteredPage(ProductSpecification.withFilters(filter),page, size,sortBy,sortDirection));
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

/*    @GetMapping("/category")
    public ResponseEntity<Page<ProductBasicDTO>> getProductFilteredForCategoriesPaged(@RequestParam("page") int page, @RequestParam("size") int size, @RequestParam("category") ProductCategory category){

            return ResponseEntity.ok(productService.getProductFilteredForCategoriesPaged(page,size,category));
    }

    @GetMapping("/category/clothing")
    public ResponseEntity<Page<ProductBasicDTO>> getClothingByTypePaged(@RequestParam("page") int page, @RequestParam("size") int size, @RequestParam("clothingType")ClothingType clothingType){
        return ResponseEntity.ok(productService.getClothingByTypePaged(page,size,clothingType));
    }
    @GetMapping("/category/entertainment")
    public ResponseEntity<Page<ProductBasicDTO>> getEntertainmentByTypePaged(@RequestParam("page") int page, @RequestParam("size") int size, @RequestParam("entertainmentType")EntertainmentLanguage entertainmentType){
        return ResponseEntity.ok(productService.getEntertainmentByTypePaged(page,size,entertainmentType));
    }

    @GetMapping("/category/home")
    public ResponseEntity<Page<ProductBasicDTO>> getHomeByTypePaged(@RequestParam("page") int page, @RequestParam("size") int size, @RequestParam("homeType")HomeSize homeType){
        return ResponseEntity.ok(productService.getHomeByTypePaged(page,size,homeType));
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
    }*/

    @GetMapping("/{id}/offers")
    public ResponseEntity<Page<OfferBasicDTO>> getProductOffers(@RequestParam("id") String id,@RequestParam("page") int page, @RequestParam("size") int size) throws IllegalAccessException {
        return ResponseEntity.ok(productService.getProductOffers(id,page,size));
    }

    @GetMapping("/{id}/messages")
    public ResponseEntity<Page<MessageDTO>> getProductMessages(@RequestParam("id") String id, @RequestParam("page") int page, @RequestParam("size") int size) throws IllegalAccessException {
        return ResponseEntity.ok(productService.getProductMessages(id,page,size));
    }
    @GetMapping("/{id}/order")
    public ResponseEntity<OrderBasicDTO> getProductOrder(@RequestParam("id") String id) throws IllegalAccessException {
        return ResponseEntity.ok(productService.getProductOrder(id));
    }

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
        return ResponseEntity.ok(productService.getProductById(tokenStore.getIdByCapability(token), true));
    }

}
