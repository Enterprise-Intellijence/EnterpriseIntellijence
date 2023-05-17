package com.enterpriseintellijence.enterpriseintellijence.data.repository;

import com.enterpriseintellijence.enterpriseintellijence.data.entities.User;
import com.enterpriseintellijence.enterpriseintellijence.dto.PaymentMethodDTO;
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



/*

    @Query("select u from User uf, User u where uf.following = ?1 and uf.followers = u.id")
    List<User> findAllFollowers(String userId);

    @Query("select u from User uf, User u where uf.followers = ?1 and uf.following = u.id")
    List<User> findAllFollowing(String userId);

    void addFollow(String userId, String userIdToFollow);
    void removeFollow(String userId, String userIdToUnFollow);

    void addLikeToProduct(String userId, String productId);
    void removeLikeToProduct(String userId, String productId);*/

}
