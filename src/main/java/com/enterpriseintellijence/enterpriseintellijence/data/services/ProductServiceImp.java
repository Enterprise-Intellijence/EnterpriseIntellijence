package com.enterpriseintellijence.enterpriseintellijence.data.services;

import com.enterpriseintellijence.enterpriseintellijence.data.entities.*;
import com.enterpriseintellijence.enterpriseintellijence.data.repository.ClothingRepository;
import com.enterpriseintellijence.enterpriseintellijence.data.repository.EntertainmentRepository;
import com.enterpriseintellijence.enterpriseintellijence.data.repository.HomeRepository;
import com.enterpriseintellijence.enterpriseintellijence.data.repository.ProductRepository;
import com.enterpriseintellijence.enterpriseintellijence.data.repository.UserRepository;
import com.enterpriseintellijence.enterpriseintellijence.dto.*;
import com.enterpriseintellijence.enterpriseintellijence.dto.basics.ProductBasicDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.basics.UserBasicDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.*;
import com.enterpriseintellijence.enterpriseintellijence.exception.IdMismatchException;

import com.enterpriseintellijence.enterpriseintellijence.security.JwtContextUtils;
import com.enterpriseintellijence.enterpriseintellijence.security.TokenStore;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
    private final ClothingRepository clothingRepository;
    private final EntertainmentRepository entertainmentRepository;
    private final HomeRepository homeRepository;
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final TokenStore tokenStore;

    private final Clock clock;


    private final JwtContextUtils jwtContextUtils;
    @Override
    public ProductDTO createProduct(ProductDTO productDTO) {
        Product product = new Product();
        try{
            product = mapToEntity(productDTO);
            product.setUploadDate(LocalDateTime.now(clock));
            product.setLikesNumber(0);
            product.setSeller(userRepository.findByUsername(jwtContextUtils.getUsernameFromContext().get()));

            //product.getDefaultImage().setDefaultProduct(product);
            if (product.getProductImages()!=null){
                for(ProductImage productImage: product.getProductImages())
                    productImage.setProduct(product);
            }


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

        User userRequesting = userRepository.findByUsername(jwtContextUtils.getUsernameFromContext().get());
        if (!oldProduct.getSeller().getUsername().equals(userRequesting.getUsername())
                && userRequesting.getRole().equals(UserRole.USER))
            throw new EntityNotFoundException("Product not found");

        product = productRepository.save(product);
        return mapToProductDetailsDTO(product);
    }

    @Override
    public ProductDTO updateProduct(String id, ProductDTO patch) {
        ProductDTO productDTO = mapToProductDetailsDTO(productRepository.findById(id).orElseThrow(EntityNotFoundException::new));

        // TODO: come implementare?

        return productDTO;
    }

    @Override
    public void deleteProduct(String id) {
        productRepository.deleteById(id);
    }

    @Override
    public ProductDTO getProductById(String id, boolean capability) {
        Product product = productRepository.findById(id)
            .orElseThrow((() ->
                new EntityNotFoundException("Product not found")
            ));
        Optional<String> username = jwtContextUtils.getUsernameFromContext();
        if(!username.isEmpty()) {
            User userRequesting = userRepository.findByUsername(username.get());
            if (product.getSeller().getUsername().equals(userRequesting.getUsername()) || capability)
                return mapToProductDetailsDTO(product);
        }

        if (product.getVisibility().equals(Visibility.PRIVATE) && !capability)
            throw new EntityNotFoundException("Product not found");
        product.setViews(product.getViews()+1);
        productRepository.save(product);
        return mapToProductDetailsDTO(product);
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
        Page<Product> products = productRepository.findAllByVisibility(Visibility.PUBLIC, PageRequest.of(page,size));//la dimensione deve arrivare tramite parametro
        List<ProductBasicDTO> collect = products.stream().map(s->modelMapper.map(s, ProductBasicDTO.class)).collect(Collectors.toList());
        return new PageImpl<>(collect);
    }

    @Override
    public Page<ProductBasicDTO> getProductFilteredForCategoriesPaged(int page, int size, ProductCategory productCategory) {
        Page<Product> products = productRepository.findAllByProductCategoryAndVisibility(productCategory,Visibility.PUBLIC,PageRequest.of(page,size));
        List<ProductBasicDTO> collect = products.stream().map(s->modelMapper.map(s, ProductBasicDTO.class)).collect(Collectors.toList());
        return new PageImpl<>(collect);
    }

    @Override
    public Page<ProductBasicDTO> getAllPagedBySellerId(UserBasicDTO userBasicDTO, int page, int size) {
        User user=modelMapper.map(userBasicDTO,User.class);
        Page<Product> products = null;

        if(jwtContextUtils.getUsernameFromContext().isPresent() && jwtContextUtils.getUsernameFromContext().get().equals(user.getUsername()))
            products= productRepository.findAllBySeller(user,PageRequest.of(page,size));
        else
            products = productRepository.findAllBySellerAndVisibilityEquals(user, Visibility.PUBLIC,PageRequest.of(page, size));
        List<ProductBasicDTO> collect = products.stream().map(s->modelMapper.map(s,ProductBasicDTO.class)).collect(Collectors.toList());

        return new PageImpl<>(collect);
    }

    @Override
    public Page<ProductBasicDTO> getClothingByTypePaged(int page, int size, ClothingType clothingType) {
        Page<Product> products = clothingRepository.findAllByClothingTypeAndVisibility(clothingType,Visibility.PUBLIC,PageRequest.of(page,size));
        List<ProductBasicDTO> collect = products.stream().map(s->modelMapper.map(s, ProductBasicDTO.class)).collect(Collectors.toList());
        return new PageImpl<>(collect);
    }

    @Override
    public Page<ProductBasicDTO> getEntertainmentByTypePaged(int page, int size, EntertainmentType entertainmentType) {
        Page<Product> products = entertainmentRepository.findAllByEntertainmentTypeAndVisibility(entertainmentType,Visibility.PUBLIC,PageRequest.of(page,size));
        List<ProductBasicDTO> collect = products.stream().map(s->modelMapper.map(s, ProductBasicDTO.class)).collect(Collectors.toList());
        return new PageImpl<>(collect);
    }

    @Override
    public Page<ProductBasicDTO> getHomeByTypePaged(int page, int size, HomeType homeType) {
        Page<Product> products = homeRepository.findAllByHomeTypeAndVisibility(homeType,Visibility.PUBLIC,PageRequest.of(page,size));
        List<ProductBasicDTO> collect = products.stream().map(s->modelMapper.map(s, ProductBasicDTO.class)).collect(Collectors.toList());
        return new PageImpl<>(collect);
    }

    @Override
    public Page<ProductBasicDTO> searchProduct(String keystring, int page, int size) {

//        List<Product> products = productRepository.search(keystring,PageRequest.of(page,size));
        Page<Product> products = productRepository.findAllByTitleContainingOrDescriptionContainingAndVisibility(keystring,keystring,Visibility.PUBLIC,PageRequest.of(page,size));

        List<ProductBasicDTO> collect = products.stream().map(s->modelMapper.map(s, ProductBasicDTO.class)).collect(Collectors.toList());
        return new PageImpl<>(collect);

    }

    @Override
    public Page<ProductBasicDTO> searchProductByPrice(Double startPrice, Double endPrice, int page, int size) {
        Page<Product> products = productRepository.getByProductByPrice(startPrice,endPrice,Visibility.PUBLIC,PageRequest.of(page,size));
        List<ProductBasicDTO> collect = products.stream().map(s->modelMapper.map(s, ProductBasicDTO.class)).collect(Collectors.toList());
        return new PageImpl<>(collect);
    }

    @Override
    public Page<ProductBasicDTO> getMostLikedProducts(int page, int size) {
        Page<Product> products = productRepository.findAllByVisibility(Visibility.PUBLIC,PageRequest.of(page,size, Sort.by(Sort.Direction.DESC,"likesNumber")));
        List<ProductBasicDTO> collect = products.stream().map(s->modelMapper.map(s, ProductBasicDTO.class)).collect(Collectors.toList());
        return new PageImpl<>(collect);
    }

    @Override
    public Page<ProductBasicDTO> getMostViewedProducts(int page, int size) {
        Page<Product> products = productRepository.findAllByVisibility(Visibility.PUBLIC,PageRequest.of(page,size, Sort.by(Sort.Direction.DESC,"views")));
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

    private List<ProductBasicDTO> mapToProductBasicDTOList(Page<Product> products) {
        return products.stream().map(s->modelMapper.map(s, ProductBasicDTO.class)).collect(Collectors.toList());
    }



    private void throwOnIdMismatch(String id, ProductDTO productDTO) {
        if (productDTO.getId() != null && !productDTO.getId().equals(id)) {
            throw new IdMismatchException();
        }
    }

    public String getCapabilityUrl(String id) {

        Optional<String> username = jwtContextUtils.getUsernameFromContext();
        if (username.isEmpty()) {
            throw new RuntimeException("User not found");
        }
        Product product = productRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        if(!product.getSeller().getUsername().equals(username.get())){
            throw new RuntimeException("Unauthorized operation");
        }
        String token = tokenStore.createCapabilityToken(id);
        return  "https://localhost:8443/api/v1/products/capability/" + token;
    }

    @Override
    public Page<UserBasicDTO> getUserThatLikedProduct(String id, int page, int size) {
        Optional<User> u = userRepository.findById(id);
        if (u.isEmpty()) {
            throw new EntityNotFoundException("Product not found");
        }
        return userRepository.findAllByLikedProducts(id, PageRequest.of(page, size))
                .map(user -> modelMapper.map(user, UserBasicDTO.class));
    }

}
