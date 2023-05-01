package com.enterpriseintellijence.enterpriseintellijence.data.services;

import com.enterpriseintellijence.enterpriseintellijence.data.entities.Product;
import com.enterpriseintellijence.enterpriseintellijence.data.repository.ProductRepository;
import com.enterpriseintellijence.enterpriseintellijence.dto.ProductDTO;
import com.enterpriseintellijence.enterpriseintellijence.exception.IdMismatchException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImp implements ProductService {

    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;

    private final Clock clock;

    @Override
    public ProductDTO createProduct(ProductDTO productDTO) {

        //productDTO.setUploadDate(LocalDateTime.now());
        Product product = mapToEntity(productDTO);
        product.setUploadDate(LocalDateTime.now(clock));
        // todo: set seller from context
        product = productRepository.save(product);
        return mapToDTO(product);
    }

    @Override
    public ProductDTO replaceProduct(String id, ProductDTO productDTO) {
        throwOnIdMismatch(id, productDTO);
        Product oldProduct = productRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        Product product = mapToEntity(productDTO);

        // TODO: 28/04/2023 add user from context and check for permission

        product = productRepository.save(product);
        return mapToDTO(product);
    }

    @Override
    public ProductDTO updateProduct(String id, ProductDTO patch) {
        ProductDTO productDTO = mapToDTO(productRepository.findById(id).orElseThrow(EntityNotFoundException::new));
        Product product = mapToEntity(productDTO);

        // TODO: come implementare?

        return mapToDTO(product);
    }

    @Override
    public void deleteProduct(String id) {
        productRepository.deleteById(id);
    }

    @Override
    public ProductDTO getProductById(String id) {
        // TODO: 27/04/2023 dovrebbe essere optional il product?
        Product product = productRepository.findById(id)
            .orElseThrow((() ->
                new EntityNotFoundException("Product not found")
            ));

        return mapToDTO(product);
    }

    @Override
    public Iterable<ProductDTO> findAll() {
        // TODO: 27/04/2023 dovrebbe essere una page?
        /*Iterable<Product> products = productRepository.findAll();
        return mapToDTO(products);*/
        // TODO: 27/04/2023 Scalzo la fa così questa(testare il funzionamento):
        return productRepository.findAll().stream()
                .map(s -> modelMapper.map(s, ProductDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public Page<ProductDTO> getAllPaged(int page, int size) {
        Page<Product> products = productRepository.findAll(PageRequest.of(page,size));//la dimensione deve arrivare tramite parametro
        List<ProductDTO> collect = products.stream().map(s->modelMapper.map(s,ProductDTO.class)).collect(Collectors.toList());
        return new PageImpl<>(collect);
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
