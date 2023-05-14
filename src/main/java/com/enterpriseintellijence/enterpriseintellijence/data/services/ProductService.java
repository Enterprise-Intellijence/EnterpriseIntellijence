package com.enterpriseintellijence.enterpriseintellijence.data.services;

import com.enterpriseintellijence.enterpriseintellijence.dto.ProductDTO;

import com.enterpriseintellijence.enterpriseintellijence.dto.enums.ProductCategory;
import org.springframework.data.domain.Page;

public interface ProductService {
    ProductDTO createProduct(ProductDTO productDTO);
    ProductDTO replaceProduct(String id, ProductDTO productDTO);
    ProductDTO updateProduct(String id, ProductDTO productDTO);
    void deleteProduct(String id);
    ProductDTO getProductById(String id);

    Iterable<ProductDTO> findAll();

    Page<ProductDTO> getAllPaged(int page, int size);

    Page<ProductDTO> getProductFilteredForCategoriesPaged(int page, int size, ProductCategory productCategory);

    String getCapabilityUrl(String id);
}
