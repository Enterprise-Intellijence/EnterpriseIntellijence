package com.enterpriseintellijence.enterpriseintellijence.service;

import com.enterpriseintellijence.enterpriseintellijence.data.entities.Product;
import com.enterpriseintellijence.enterpriseintellijence.data.repository.ProductRepository;
import com.enterpriseintellijence.enterpriseintellijence.dto.ProductDTO;
import com.enterpriseintellijence.enterpriseintellijence.exception.IdMismatchException;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    private final ModelMapper modelMapper;

    public ProductDTO createProduct(ProductDTO productDTO) {

        productDTO.setUploadDate(LocalDateTime.now());
        productDTO.setSendDate(LocalDateTime.now());
        Product product = modelMapper.map(productDTO,Product.class);
        product = productRepository.save(product);

        return (modelMapper.map(product, ProductDTO.class));
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
