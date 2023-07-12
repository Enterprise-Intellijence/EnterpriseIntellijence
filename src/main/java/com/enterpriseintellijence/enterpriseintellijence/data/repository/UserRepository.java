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

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(value = "insert into user_likes values (?2, ?1)", nativeQuery = true)
    void addLikeToProduct(String userId, String productId);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(value = "delete from user_likes where user_id = ?1 and product_id = ?2", nativeQuery = true)
    void removeLikeToProduct(String userId, String productId);

    Page<User> findAllByLikedProducts(String productId, Pageable pageable);

}
