package com.enterpriseintellijence.enterpriseintellijence.data.repository;

import com.enterpriseintellijence.enterpriseintellijence.data.entities.Following;
import com.enterpriseintellijence.enterpriseintellijence.data.entities.Offer;
import com.enterpriseintellijence.enterpriseintellijence.data.entities.User;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;

import java.util.Optional;

public interface FollowingRepository extends JpaRepositoryImplementation<Following, String>, JpaSpecificationExecutor<Following> {
    Following findByFollowingEquals(User following);
    Boolean existsFollowingByFollowerEqualsAndFollowingEquals(User follower,User following);

}
