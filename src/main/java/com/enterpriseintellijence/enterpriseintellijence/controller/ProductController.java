package com.enterpriseintellijence.enterpriseintellijence.controller;

import com.enterpriseintellijence.enterpriseintellijence.data.entities.ProductCategory;
import com.enterpriseintellijence.enterpriseintellijence.data.entities.Size;
import com.enterpriseintellijence.enterpriseintellijence.data.repository.SizeRepository;
import com.enterpriseintellijence.enterpriseintellijence.data.repository.UserRepository;
import com.enterpriseintellijence.enterpriseintellijence.data.services.ProductService;
import com.enterpriseintellijence.enterpriseintellijence.data.specification.ProductSpecification;
import com.enterpriseintellijence.enterpriseintellijence.dto.MessageDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.ProductCategoryDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.SizeDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.basics.OfferBasicDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.basics.OrderBasicDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.basics.ProductBasicDTO;

import com.enterpriseintellijence.enterpriseintellijence.dto.ProductDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.basics.UserBasicDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.creation.ProductCreateDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.*;
import com.enterpriseintellijence.enterpriseintellijence.security.TokenStore;
import com.nimbusds.jose.JOSEException;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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
    private final SizeRepository sizeRepository;

    @PostMapping(consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public ProductDTO createProduct(@RequestBody @Valid ProductCreateDTO productCreateDTO) throws IllegalAccessException {

        return productService.createProduct(productCreateDTO);
    }

    @PutMapping(path = "/{id}",consumes="application/json")
    public ResponseEntity<ProductDTO> replaceProduct(@PathVariable("id") String id, @Valid @RequestBody ProductDTO productDTO) throws IllegalAccessException {
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

    @GetMapping("/me")
    public ResponseEntity<Page<ProductBasicDTO>> getMyProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size){
        return ResponseEntity.ok(productService.getMyProducts(page,size));
    }

    @GetMapping("/{id}/offers")
    public ResponseEntity<Page<OfferBasicDTO>> getProductOffers(@PathVariable("id") String id,@RequestParam("page") int page, @RequestParam("size") int size) throws IllegalAccessException {
        return ResponseEntity.ok(productService.getProductOffers(id,page,size));
    }

    @GetMapping("/{id}/messages")
    public ResponseEntity<Page<MessageDTO>> getProductMessages(@PathVariable("id") String id, @RequestParam("page") int page, @RequestParam("size") int size) throws IllegalAccessException {
        return ResponseEntity.ok(productService.getProductMessages(id,page,size));
    }
    @GetMapping("/{id}/order")
    public ResponseEntity<OrderBasicDTO> getProductOrder(@PathVariable("id") String id) throws IllegalAccessException {
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

    @GetMapping("/categories")
    public ResponseEntity<Iterable<ProductCategoryDTO>> getCategoriesList()
    {
        return ResponseEntity.ok(productService.getCategoriesList());
    }

    @GetMapping("/sizes")
    public ResponseEntity<Iterable<SizeDTO>> getSizesList()
    {
        return ResponseEntity.ok(productService.getSizeList());
    }

    @GetMapping("/filter")
    public ResponseEntity<Page<ProductBasicDTO>> getFilteredProducts(
            // TODO: 03/06/2023 cambiare nome a like e view
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) Double minProductCost,
            @RequestParam(required = false) Double maxProductCost,
            @RequestParam(required = false) String[] brands,
            @RequestParam(required = false) Condition condition,
            @RequestParam(required = false) Integer views,
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) LocalDateTime uploadDate,
            @RequestParam(required = false) Availability availability,
            @RequestParam(required = false) ProductCategory productCategory,
            @RequestParam(required = false) String primaryCat,
            @RequestParam(required = false) String secondaryCat,
            @RequestParam(required = false) String tertiaryCat,
            @RequestParam(required = false) Integer likesNumber,
            @RequestParam(required = false) ProductGender productGender,
            @RequestParam(required = false) String[] sizes,
            @RequestParam(required = false) Colour colour,
            @RequestParam(required = false) EntertainmentLanguage entertainmentLanguage,
            @RequestParam(required = false) HomeMaterial homeMaterial,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int sizePage,
            @RequestParam(defaultValue = "uploadDate",required = false) String sortBy,
            @RequestParam(required = false, defaultValue = "DESC") String sortDirection
    ) {
        ProductSpecification.Filter filter = new ProductSpecification.Filter(userRepository,sizeRepository );
        //attributi comuni
        filter.setTitle(title);
        filter.setDescription(description);
        filter.setMinProductCost(minProductCost);
        filter.setMaxProductCost(maxProductCost);
        filter.setBrands(brands != null ? Arrays.asList(brands) : null);
        filter.setCondition(condition);
        filter.setViews(views);
        filter.setSeller(userId);
        filter.setUploadDate(uploadDate);
        filter.setAvailability(availability);
        filter.setProductCategory(productCategory);
        filter.setPrimaryCat(primaryCat);
        filter.setSecondaryCat(secondaryCat);
        filter.setTertiaryCat(tertiaryCat);


        filter.setProductGender(productGender);
        filter.setLikesNumber(likesNumber);
        filter.setSizes(sizes != null ? Arrays.asList(sizes) :null);
        filter.setColour(colour);
        filter.setEntertainmentLanguage(entertainmentLanguage);
        filter.setHomeMaterial(homeMaterial);

        //Specification<Product> specification = ProductSpecification.withFilters(filter);

        return ResponseEntity.ok(productService.getProductFilteredPage(
                ProductSpecification.withFilters(filter),page, sizePage,sortBy,sortDirection));
    }

}
