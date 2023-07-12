package com.enterpriseintellijence.enterpriseintellijence.data.repository;

import com.enterpriseintellijence.enterpriseintellijence.data.entities.User;
import com.enterpriseintellijence.enterpriseintellijence.dto.enums.UserRole;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User,String>, JpaSpecificationExecutor<User> {
    User findByUsername(String username);

    User findByEmail(String email);

    Page<User> findAllByUsernameContainingIgnoreCase(Pageable pageable, String username);

    Page<User> findAllByRoleEqualsAndUsernameContains(Pageable pageable, UserRole userRole,String username);

    Page<User> findAllByRoleEqualsOrRoleEquals(Pageable pageable, UserRole userRole, UserRole userRole1);

/*    Page<User> findAllByFollowingId(String userId, Pageable pageable);

    Page<User> findAllByFollowersId(String userId, Pageable pageable);


    @Query("update User u set u.followers_number = u.followers_number + 1 where u.id = ?1")
    void increaseFollowersNumber(String userId);

    @Query("update User u set u.following_number = u.following_number + 1 where u.id = ?1")
    void increaseFollowingNumber(String userId);

    @Query("update User u set u.followers_number = u.followers_number - 1 where u.id = ?1")
    void decreaseFollowersNumbers(String userId);

    @Query("update User u set u.following_number = u.following_number - 1 where u.id = ?1")
    void decreaseFollowingNumbers(String userId);


    @Query(value = "insert into user_follows (user_id, following_id) values (?1, ?2)", nativeQuery = true)
    void addFollow(String userId, String userIdToFollow);


    @Query(value = "delete from user_follows where user_id = ?1 and following_id = ?2", nativeQuery = true)
    void removeFollow(String userId, String userIdToUnFollow);
*/
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(value = "insert into user_likes values (?2, ?1)", nativeQuery = true)
    void addLikeToProduct(String userId, String productId);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(value = "delete from user_likes where user_id = ?1 and product_id = ?2", nativeQuery = true)
    void removeLikeToProduct(String userId, String productId);

    Page<User> findAllByLikedProducts(String productId, Pageable pageable);
    /*
    @Query(value = "select u from users_following uf, users u where uf.followers_id = ?1 and uf.following_id = u.id", nativeQuery = true)
    Page<User> findAllFollowing(Long userId, Pageable pageable);


    @Query(value = "select p from user_likes ul, products p where ul.user_id = ?1 and ul.product_id = p.id", nativeQuery = true)
    Page<Product> findAllLikedProducts(String userId, Pageable pageable);
*/

}
