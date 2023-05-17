package com.enterpriseintellijence.enterpriseintellijence.data.repository;

import com.enterpriseintellijence.enterpriseintellijence.data.entities.User;
import com.enterpriseintellijence.enterpriseintellijence.dto.PaymentMethodDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User,String>, JpaSpecificationExecutor<User> , PagingAndSortingRepository<User,String> {
    User findByUsername(String username);


/*
    List<User> findAllUsersThatFollowUser(String userId);
    List<User> findAllUsersThatUserFollows(String userId);
    void addFollow(String userId, String userIdToFollow);
    void removeFollow(String userId, String userIdToUnFollow);

    void addLikeToProduct(String userId, String productId);
    void removeLikeToProduct(String userId, String productId);*/

}
