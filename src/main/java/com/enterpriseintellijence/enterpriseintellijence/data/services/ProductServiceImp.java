package com.enterpriseintellijence.enterpriseintellijence.data.services;

import com.enterpriseintellijence.enterpriseintellijence.data.entities.Entertainment;
import com.enterpriseintellijence.enterpriseintellijence.data.entities.Home;
import com.enterpriseintellijence.enterpriseintellijence.data.entities.Product;
import com.enterpriseintellijence.enterpriseintellijence.data.entities.Clothing;
import com.enterpriseintellijence.enterpriseintellijence.data.repository.ProductRepository;
import com.enterpriseintellijence.enterpriseintellijence.dto.*;
import com.enterpriseintellijence.enterpriseintellijence.dto.basics.ProductBasicDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.ProductCategory;
import com.enterpriseintellijence.enterpriseintellijence.exception.IdMismatchException;

import com.enterpriseintellijence.enterpriseintellijence.security.JwtContextUtils;
import com.enterpriseintellijence.enterpriseintellijence.security.TokenStore;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImp implements ProductService {

    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;

    private final Clock clock;

    @Bean
    private JwtContextUtils jwtContextUtils(){
        return new JwtContextUtils();
    }
    @Override
    public ProductDTO createProduct(ProductDTO productDTO) {
        Product product = new Product();
        try{
            product = mapToEntity(productDTO);
            product.setUploadDate(LocalDateTime.now(clock));
            jwtContextUtils().getUsernameFromContext();
            // todo: set seller from context
            product = productRepository.save(product);
        }catch (Exception e){
            e.printStackTrace();
        }

        return mapToProductDetailsDTO(product);
    }

    @Override
    public ProductDTO replaceProduct(String id, ProductDTO productDTO) {
        throwOnIdMismatch(id, productDTO);
        Product oldProduct = productRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        Product product = mapToEntity(productDTO);

        // TODO: 28/04/2023 add user from context and check for permission

        product = productRepository.save(product);
        return mapToProductDetailsDTO(product);
    }

    @Override
    public ProductDTO updateProduct(String id, ProductDTO patch) {
        ProductDTO productDTODTO = mapToProductDetailsDTO(productRepository.findById(id).orElseThrow(EntityNotFoundException::new));
        Product product = mapToEntity(productDTODTO);

        // TODO: come implementare?

        return mapToProductDetailsDTO(product);
    }

    @Override
    public void deleteProduct(String id) {
        productRepository.deleteById(id);
    }

    @Override
    public ProductDTO getProductById(String id) {
        // TODO: 15/05/2023 boolean for capability 
        Product product = productRepository.findById(id)
            .orElseThrow((() ->
                new EntityNotFoundException("Product not found")
            ));
        ProductDTO productDTO = mapToProductDetailsDTO(product);
        return productDTO;
    }

    @Override
    public Iterable<ProductBasicDTO> findAll() {
        // TODO: 01/05/2023 da sistemare ereditarietà
        return productRepository.findAll().stream()
                .map(s -> modelMapper.map(s, ProductBasicDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public Page<ProductBasicDTO> getAllPaged(int page, int size) {
        // TODO: 01/05/2023 da sistemare ereditarietà
        Page<Product> products = productRepository.findAll(PageRequest.of(page,size));//la dimensione deve arrivare tramite parametro
        List<ProductBasicDTO> collect = products.stream().map(s->modelMapper.map(s, ProductBasicDTO.class)).collect(Collectors.toList());
        return new PageImpl<>(collect);
    }

    @Override
    public Page<ProductBasicDTO> getProductFilteredForCategoriesPaged(int page, int size, ProductCategory productCategory) {
        Page<Product> products = productRepository.findAllByProductCategory(productCategory,PageRequest.of(page,size));
        List<ProductBasicDTO> collect = products.stream().map(s->modelMapper.map(s, ProductBasicDTO.class)).collect(Collectors.toList());
        return new PageImpl<>(collect);
    }




    private Product mapToEntity(ProductDTO productDTO) {
        if(productDTO.getProductCategory().equals(ProductCategory.CLOTHING))
            return modelMapper.map(productDTO, Clothing.class);
        else if(productDTO.getProductCategory().equals(ProductCategory.HOME))
            return modelMapper.map(productDTO, Home.class);
        else if(productDTO.getProductCategory().equals(ProductCategory.ENTERTAINMENT))
            return modelMapper.map(productDTO, Entertainment.class);
        else
            return modelMapper.map(productDTO,Product.class);
    }

    private ProductDTO mapToProductDetailsDTO(Product product) {
        if(product.getProductCategory().equals(ProductCategory.CLOTHING))
            return modelMapper.map(product, ClothingDTO.class);
        else if(product.getProductCategory().equals(ProductCategory.HOME))
            return modelMapper.map(product, HomeDTO.class);
        else if(product.getProductCategory().equals(ProductCategory.ENTERTAINMENT))
            return modelMapper.map(product, EntertainmentDTO.class);
        else
            return modelMapper.map(product, ProductDTO.class);
    }

    private ProductBasicDTO mapToProductDTO(Product product) {
            return modelMapper.map(product, ProductBasicDTO.class);
    }



    private void throwOnIdMismatch(String id, ProductDTO productDTO) {
        if (productDTO.getId() != null && !productDTO.getId().equals(id)) {
            throw new IdMismatchException();
        }
    }

    public String getCapabilityUrl(String id) {

        Optional<String> username = jwtContextUtils().getUsernameFromContext();
        if (username.isEmpty()) {
            throw new RuntimeException("User not found");
        }
        Product product = productRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        if(!product.getSeller().getUsername().equals(username.get())){
            throw new RuntimeException("Unauthorized operation");
        }
        String token = TokenStore.getInstance().createCapabilityToken(id);
        return  "https://localhost:8443/api/v1/products/capability/" + token;
    }
}
