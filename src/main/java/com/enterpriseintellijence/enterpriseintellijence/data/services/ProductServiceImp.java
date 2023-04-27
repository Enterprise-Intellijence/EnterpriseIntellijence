package com.enterpriseintellijence.enterpriseintellijence.data.services;

import com.enterpriseintellijence.enterpriseintellijence.data.entities.Product;
import com.enterpriseintellijence.enterpriseintellijence.data.repository.ProductRepository;
import com.enterpriseintellijence.enterpriseintellijence.dto.ProductDTO;
import com.enterpriseintellijence.enterpriseintellijence.exception.IdMismatchException;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImp implements ProductService {

    private final ProductRepository productRepository;

    private final ModelMapper modelMapper;

    public ProductDTO createProduct(ProductDTO productDTO) {

        productDTO.setUploadDate(LocalDateTime.now());
        // todo: set seller
        Product product = mapToEntity(productDTO);
        product = productRepository.save(product);
        return mapToDTO(product);
    }


    public ProductDTO replaceProduct(String id, ProductDTO productDTO) {
        throwOnIdMismatch(id, productDTO);
        Product product = mapToEntity(productDTO);
        product = productRepository.save(product);
        return mapToDTO(product);
    }

    public ProductDTO updateProduct(String id, ProductDTO productDTO) {
        throwOnIdMismatch(id, productDTO);
        Product product = mapToEntity(productDTO);

        // TODO: come implementare?
        // https://www.baeldung.com/spring-rest-json-patch

        return mapToDTO(product);
    }

    public void deleteProduct(String id) {
        // todo: check if product exists??
        productRepository.deleteById(id);
    }

    public ProductDTO getProductById(String id) {
        // TODO: 27/04/2023 dovrebbe essere optional il product?
        Product product = productRepository.findById(id)
            .orElseThrow((() ->
                new EntityNotFoundException("Product not found")
            ));

        return mapToDTO(product);
    }

    public Iterable<ProductDTO> findAll() {
        // TODO: 27/04/2023 dovrebbe essere una page?
        /*Iterable<Product> products = productRepository.findAll();
        return mapToDTO(products);*/
        // TODO: 27/04/2023 Scalzo la fa così questa(testare il funzionamento):
        return productRepository.findAll().stream()
                .map(s -> modelMapper.map(s, ProductDTO.class))
                .collect(Collectors.toList());
    }


    // TODO: VA TESTATA ASSOLUTAMENTE
    private Iterable<ProductDTO> mapToDTO(Iterable<Product> products) {
        Iterable<ProductDTO> productDTOs = new ArrayList<>();
        modelMapper.map(products,productDTOs);
        return productDTOs;
    }

    private Product mapToEntity(ProductDTO productDTO) {
        return modelMapper.map(productDTO,Product.class);
    }

    private ProductDTO mapToDTO(Product product) {
        return modelMapper.map(product,ProductDTO.class);
    }



    private void throwOnIdMismatch(String id, ProductDTO productDTO) {
        if (productDTO.getId() != null && !productDTO.getId().equals(id)) {
            throw new IdMismatchException();
        }
    }
}
