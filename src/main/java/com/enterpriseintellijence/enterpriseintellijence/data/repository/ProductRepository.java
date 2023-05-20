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

@Repository
public interface ProductRepository extends JpaRepository<Product,String>, JpaSpecificationExecutor<Product> {
    Page<Product> findAllByProductCategory(ProductCategory productCategories, Pageable pageable);
    Page<Product> findAllBySellerAndVisibilityEquals(User user, Visibility visibility,Pageable pageable);
    Page<Product> findAllBySeller(User user,Pageable pageable);

    @Query("update Product p set p.likesNumber = p.likesNumber + 1 where p.id = ?1")
    void increaseLikesNumber(String productId);

    @Query("update Product p set p.likesNumber = p.likesNumber - 1 where p.id = ?1")
    void decreaseLikesNumber(String productId);

    Page<Product> findAllByUsersThatLiked(User user, Pageable pageable);
    //@Query(value = "select u from users u, user_likes ulp where ulp.product_id = ?1 and ulp.user_id = u.id", nativeQuery = true)
    //Page<User> findAllUsersThatLikedProduct(String productId, Pageable pageable);

}
