package com.enterpriseintellijence.enterpriseintellijence.data.services;

import com.enterpriseintellijence.enterpriseintellijence.data.entities.Entertainment;
import com.enterpriseintellijence.enterpriseintellijence.data.entities.Home;
import com.enterpriseintellijence.enterpriseintellijence.data.entities.Product;
import com.enterpriseintellijence.enterpriseintellijence.data.entities.Clothing;
import com.enterpriseintellijence.enterpriseintellijence.data.repository.ProductRepository;
import com.enterpriseintellijence.enterpriseintellijence.dto.*;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.ProductCategory;
import com.enterpriseintellijence.enterpriseintellijence.exception.IdMismatchException;

import com.enterpriseintellijence.enterpriseintellijence.security.JwtContextUtils;
import com.enterpriseintellijence.enterpriseintellijence.security.TokenStore;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
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

    @Override
    public ProductFullDTO createProduct(ProductFullDTO productFullDTO) {
        Product product = new Product();
        try{
            product = mapToEntity(productFullDTO);
            product.setUploadDate(LocalDateTime.now(clock));
            JwtContextUtils.getUsernameFromContext();
            // todo: set seller from context
            product = productRepository.save(product);
        }catch (Exception e){
            e.printStackTrace();
        }

        return mapToProductDetailsDTO(product);
    }

    @Override
    public ProductFullDTO replaceProduct(String id, ProductFullDTO productFullDTO) {
        throwOnIdMismatch(id, productFullDTO);
        Product oldProduct = productRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        Product product = mapToEntity(productFullDTO);

        // TODO: 28/04/2023 add user from context and check for permission

        product = productRepository.save(product);
        return mapToProductDetailsDTO(product);
    }

    @Override
    public ProductFullDTO updateProduct(String id, ProductFullDTO patch) {
        ProductFullDTO productFullDTODTO = mapToProductDetailsDTO(productRepository.findById(id).orElseThrow(EntityNotFoundException::new));
        Product product = mapToEntity(productFullDTODTO);

        // TODO: come implementare?

        return mapToProductDetailsDTO(product);
    }

    @Override
    public void deleteProduct(String id) {
        productRepository.deleteById(id);
    }

    @Override
    public ProductFullDTO getProductById(String id) {
        // TODO: 15/05/2023 boolean for capability 
        Product product = productRepository.findById(id)
            .orElseThrow((() ->
                new EntityNotFoundException("Product not found")
            ));
        ProductFullDTO productFullDTO = mapToProductDetailsDTO(product);
        return productFullDTO;
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




    private Product mapToEntity(ProductFullDTO productFullDTO) {
        if(productFullDTO.getProductCategory().equals(ProductCategory.CLOTHING))
            return modelMapper.map(productFullDTO, Clothing.class);
        else if(productFullDTO.getProductCategory().equals(ProductCategory.HOME))
            return modelMapper.map(productFullDTO, Home.class);
        else if(productFullDTO.getProductCategory().equals(ProductCategory.ENTERTAINMENT))
            return modelMapper.map(productFullDTO, Entertainment.class);
        else
            return modelMapper.map(productFullDTO,Product.class);
    }

    private ProductFullDTO mapToProductDetailsDTO(Product product) {
        if(product.getProductCategory().equals(ProductCategory.CLOTHING))
            return modelMapper.map(product, ClothingDTO.class);
        else if(product.getProductCategory().equals(ProductCategory.HOME))
            return modelMapper.map(product, HomeDTO.class);
        else if(product.getProductCategory().equals(ProductCategory.ENTERTAINMENT))
            return modelMapper.map(product, EntertainmentDTO.class);
        else
            return modelMapper.map(product, ProductFullDTO.class);
    }

    private ProductBasicDTO mapToProductDTO(Product product) {
            return modelMapper.map(product, ProductBasicDTO.class);
    }



    private void throwOnIdMismatch(String id, ProductFullDTO productFullDTO) {
        if (productFullDTO.getId() != null && !productFullDTO.getId().equals(id)) {
            throw new IdMismatchException();
        }
    }

    public String getCapabilityUrl(String id) {

        Optional<String> username = JwtContextUtils.getUsernameFromContext();
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
