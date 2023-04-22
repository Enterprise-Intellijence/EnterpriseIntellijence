package com.enterpriseintellijence.enterpriseintellijence.data.services;

import com.enterpriseintellijence.enterpriseintellijence.data.entities.Product;
import com.enterpriseintellijence.enterpriseintellijence.dto.ProductDTO;

import java.time.LocalDateTime;

public interface ProductService {
    ProductDTO createProduct(ProductDTO productDTO);
    ProductDTO replaceProduct(String id, ProductDTO productDTO);
    ProductDTO updateProduct(String id, ProductDTO productDTO);
    void deleteProduct(String id);
    ProductDTO getProductById(String id);
    Iterable<ProductDTO> findAll();
}
