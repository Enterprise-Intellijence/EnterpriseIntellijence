package com.enterpriseintellijence.enterpriseintellijence.data.repository;

import com.enterpriseintellijence.enterpriseintellijence.data.entities.Following;
import com.enterpriseintellijence.enterpriseintellijence.data.entities.Offer;
import com.enterpriseintellijence.enterpriseintellijence.data.entities.User;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FollowingRepository extends JpaRepositoryImplementation<Following, String>, JpaSpecificationExecutor<Following> {
    Following findByFollowerEqualsAndFollowingEquals(User follower,User following);
    Boolean existsFollowingByFollowerEqualsAndFollowingEquals(User follower,User following);

}
