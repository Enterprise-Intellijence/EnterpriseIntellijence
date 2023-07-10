package com.enterpriseintellijence.enterpriseintellijence.data.services;

import com.enterpriseintellijence.enterpriseintellijence.data.entities.Product;
import com.enterpriseintellijence.enterpriseintellijence.dto.*;
import com.enterpriseintellijence.enterpriseintellijence.dto.basics.OfferBasicDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.basics.OrderBasicDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.basics.ProductBasicDTO;

import com.enterpriseintellijence.enterpriseintellijence.dto.basics.UserBasicDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.creation.ProductCreateDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;

public interface ProductService {
    ProductDTO createProduct(ProductCreateDTO productCreateDTO) throws IllegalAccessException;
    ProductDTO updateProduct(String id, ProductDTO productDTO) throws IllegalAccessException;
    void deleteProduct(String id) throws IllegalAccessException;

    ProductDTO getProductById(String id, boolean capability);

    String getCapabilityUrl(String productId);

    CapabilityDTO getCapability(String productId);
    

    Page<UserBasicDTO> getUserThatLikedProduct(String id, int page, int size);

    Page<OfferBasicDTO> getProductOffers(String id, int page, int size) throws IllegalAccessException;

    Page<MessageDTO> getProductMessages(String id, int page, int size) throws IllegalAccessException;


    Iterable<ProductCategoryDTO> getCategoriesList();

    Iterable<String> getPrimaryCategoriesList();

    Iterable<String> getSecondaryCategoriesListByPrimaryCat(String primaryCategory);

    Iterable<String> getTertiaryCategoriesListBySecondaryCat(String secondaryCategory);
    String getCategoryId(String category);
    Iterable<SizeDTO> getSizeList();

    Iterable<String> getSizeListByCategory(String category);

    Page<ProductBasicDTO> getProductFilteredPage(Specification<Product> withFilters, int page, int size,String sortBy,String sortDirection);

    Page<ProductBasicDTO> getMyProducts(int page, int size);

    ProductBasicDTO getProductBasicById(String id, boolean capability);

    String getCapabilityToken(String productId);
}
