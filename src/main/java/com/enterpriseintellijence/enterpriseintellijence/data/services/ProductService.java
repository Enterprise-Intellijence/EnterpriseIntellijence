package com.enterpriseintellijence.enterpriseintellijence.data.services;

import com.enterpriseintellijence.enterpriseintellijence.dto.basics.ProductBasicDTO;

import com.enterpriseintellijence.enterpriseintellijence.dto.ProductDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.basics.UserBasicDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.ClothingType;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.EntertainmentType;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.HomeType;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.ProductCategory;
import jakarta.persistence.EnumType;
import org.springframework.data.domain.Page;

public interface ProductService {
    ProductDTO createProduct(ProductDTO productDTO);
    ProductDTO replaceProduct(String id, ProductDTO productDTO);
    ProductDTO updateProduct(String id, ProductDTO productDTO);
    void deleteProduct(String id);

    ProductDTO getProductById(String id, boolean capability);

    Iterable<ProductBasicDTO> findAll();

    Page<ProductBasicDTO> getAllPaged(int page, int size);

    Page<ProductBasicDTO> getProductFilteredForCategoriesPaged(int page, int size, ProductCategory productCategory);

    String getCapabilityUrl(String id);

    Page<ProductBasicDTO> getAllPagedBySellerId(UserBasicDTO userBasicDTO, int page, int size);

    Page<ProductBasicDTO> getClothingByTypePaged(int page, int size, ClothingType clothingType);

    Page<ProductBasicDTO> getEntertainmentByTypePaged(int page, int size, EntertainmentType entertainmentType);

    Page<ProductBasicDTO> getHomeByTypePaged(int page, int size, HomeType homeType);


    Page<ProductBasicDTO> searchProduct(String keystring,int page, int size);

    Page<ProductBasicDTO> searchProductByPrice(Double startPrice, Double endPrice, int page, int size);

    Page<ProductBasicDTO> getMostLikedProducts(int page, int size);

    Page<ProductBasicDTO> getMostViewedProducts(int page, int size);

    Page<UserBasicDTO> getUserThatLikedProduct(String id, int page, int size);

}
