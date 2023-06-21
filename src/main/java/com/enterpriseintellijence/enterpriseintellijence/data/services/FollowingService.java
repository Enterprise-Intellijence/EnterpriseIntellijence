package com.enterpriseintellijence.enterpriseintellijence.data.services;

import com.enterpriseintellijence.enterpriseintellijence.data.entities.Following;
import com.enterpriseintellijence.enterpriseintellijence.dto.FollowingFollowersDTO;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

public interface FollowingService {
    FollowingFollowersDTO follow(String id) throws IllegalAccessException;

    void unfollow(String id) throws IllegalAccessException;

    Page<FollowingFollowersDTO> getFollowingByUser(String id, int page, int sizePage);

    Page<FollowingFollowersDTO> getFollowersOfUser(String id, int page, int sizePage);

    ResponseEntity<Boolean> imFollowingThisUser(String userId);
}
