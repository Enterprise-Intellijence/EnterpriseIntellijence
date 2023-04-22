package com.enterpriseintellijence.enterpriseintellijence.service;

import com.enterpriseintellijence.enterpriseintellijence.dto.ProductDTO;
import com.enterpriseintellijence.enterpriseintellijence.exception.IdMismatchException;

import org.springframework.stereotype.Service;

@Service
public class ProductService {
    public ProductDTO createProduct(ProductDTO productDTO) {

        // TODO: 21/04/2023
        return new ProductDTO();
    }

    public ProductDTO replaceProduct(String id, ProductDTO productDTO) {
        throwOnIdMismatch(id, productDTO);
        // TODO: 21/04/2023
        return null;
    }

    public ProductDTO updateProduct(String id, ProductDTO productDTO) {
        throwOnIdMismatch(id, productDTO);
        // TODO: 21/04/2023
        return null;
    }

    public void deleteProduct(String id) {
        // TODO: 21/04/2023  
    }

    public ProductDTO getProductById(String id) {
        // TODO: 21/04/2023
        return null;
    }

    public Iterable<ProductDTO> findAll() {
        return null;
    }


    private void throwOnIdMismatch(String id, ProductDTO productDTO) {
        if (productDTO.getId() != null && !productDTO.getId().equals(id)) {
            throw new IdMismatchException();
        }
    }
}
