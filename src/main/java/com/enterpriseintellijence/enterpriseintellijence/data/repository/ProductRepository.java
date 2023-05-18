package com.enterpriseintellijence.enterpriseintellijence.data.repository;

import com.enterpriseintellijence.enterpriseintellijence.data.entities.Product;
import com.enterpriseintellijence.enterpriseintellijence.data.entities.User;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.ProductCategory;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.Visibility;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product,String>, JpaSpecificationExecutor<Product> {
    Page<Product> findAllByProductCategory(ProductCategory productCategories, Pageable pageable);
    Page<Product> findAllBySellerAndVisibilityEquals(User user, Visibility visibility,Pageable pageable);
    Page<Product> findAllBySeller(User user,Pageable pageable);

    @Query(value = "SELECT * from Product p where p.title like %?1% or p.description like %?1%", nativeQuery = true)
    List<Product> search(String keyword, Pageable pageable);

/*
    Page<User> findAllUsersThatLikedProduct(Product product, Pageable pageable);*/

}
