package com.enterpriseintellijence.enterpriseintellijence.data.services;

import com.enterpriseintellijence.enterpriseintellijence.dto.ProductBasicDTO;

import com.enterpriseintellijence.enterpriseintellijence.dto.ProductFullDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.ProductCategory;
import org.springframework.data.domain.Page;

public interface ProductService {
    ProductFullDTO createProduct(ProductFullDTO productFullDTO);
    ProductFullDTO replaceProduct(String id, ProductFullDTO productFullDTO);
    ProductFullDTO updateProduct(String id, ProductFullDTO productFullDTO);
    void deleteProduct(String id);
    ProductFullDTO getProductById(String id);

    Iterable<ProductBasicDTO> findAll();

    Page<ProductBasicDTO> getAllPaged(int page, int size);

    Page<ProductBasicDTO> getProductFilteredForCategoriesPaged(int page, int size, ProductCategory productCategory);

    String getCapabilityUrl(String id);
}
