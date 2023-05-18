package com.enterpriseintellijence.enterpriseintellijence.data.repository;

import com.enterpriseintellijence.enterpriseintellijence.data.entities.Product;
import com.enterpriseintellijence.enterpriseintellijence.data.entities.User;
import org.hibernate.query.sqm.mutation.internal.cte.CteInsertStrategy;
import org.hibernate.query.sqm.mutation.internal.cte.CteMutationStrategy;
import com.enterpriseintellijence.enterpriseintellijence.dto.PaymentMethodDTO;
import org.hibernate.annotations.NamedNativeQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User,String>, JpaSpecificationExecutor<User> , PagingAndSortingRepository<User,String> {
    User findByUsername(String username);

    @Query(value = "select u from users_following uf, users u where uf.following_id = ?1 and uf.followers_id = u.id", nativeQuery = true)
    Page<User> findAllFollowers(String userId, Pageable pageable);


    @Query(value = "select u from users_following uf, users u where uf.followers_id = ?1 and uf.following_id = u.id", nativeQuery = true)
    Page<User> findAllFollowing(String userId, Pageable pageable);


    @Query(value = "insert into users_following (followers_id, following_id) values (?1, ?2)", nativeQuery = true)
    void addFollow(String userId, String userIdToFollow);

    void increaseFollowersNumber(String userId);

    void increaseFollowingNumber(String userId);

    @Query(value = "delete from users_following where followers_id = ?1 and following_id = ?2", nativeQuery = true)
    void removeFollow(String userId, String userIdToUnFollow);

    void decreaseFollowersNumbers(String userId);

    void decreaseFollowingNumbers(String userId);


    @Query(value = "insert into user_likes values (?1, ?2)", nativeQuery = true)
    void addLikeToProduct(String userId, String productId);

    @Query(value = "delete from user_likes where user_id = ?1 and product_id = ?2", nativeQuery = true)
    void removeLikeToProduct(String userId, String productId);

    @Query(value = "select p from user_likes ul, products p where ul.user_id = ?1 and ul.product_id = p.id", nativeQuery = true)
    Page<Product> findAllLikedProducts(String userId, Pageable pageable);

}
