package com.enterpriseintellijence.enterpriseintellijence.data.services;

import com.enterpriseintellijence.enterpriseintellijence.data.entities.Product;
import com.enterpriseintellijence.enterpriseintellijence.dto.ProductDTO;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;

public interface ProductService {
    ProductDTO createProduct(ProductDTO productDTO);
    ProductDTO replaceProduct(String id, ProductDTO productDTO);
    ProductDTO updateProduct(String id, JsonPatch patch) throws JsonPatchException;
    void deleteProduct(String id);
    ProductDTO getProductById(String id);

    Iterable<ProductDTO> findAll();

    Page<ProductDTO> getAllPaged(int page, int size);
}
