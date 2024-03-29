package com.enterpriseintellijence.enterpriseintellijence.data.services;

import com.enterpriseintellijence.enterpriseintellijence.data.entities.Following;
import com.enterpriseintellijence.enterpriseintellijence.data.entities.User;
import com.enterpriseintellijence.enterpriseintellijence.data.repository.FollowingRepository;
import com.enterpriseintellijence.enterpriseintellijence.data.repository.UserRepository;
import com.enterpriseintellijence.enterpriseintellijence.dto.FollowingFollowersDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.basics.UserBasicDTO;
import com.enterpriseintellijence.enterpriseintellijence.security.JwtContextUtils;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FollowingServiceImp implements FollowingService{
    private final JwtContextUtils jwtContextUtils;
    private final UserRepository userRepository;
    private final FollowingRepository followingRepository;
    private final NotificationService notificationService;
    private final ModelMapper modelMapper;
    private final Clock clock;


    @Override
    @Transactional
    public FollowingFollowersDTO follow(String id) throws IllegalAccessException {
        User loggedUser = jwtContextUtils.getUserLoggedFromContext();

        if(loggedUser.getId().equals(id))
            throw new IllegalAccessException("You can't follow yourself");

        User followingUser = userRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        if(!followingRepository.existsFollowingByFollowerEqualsAndFollowingEquals(loggedUser,followingUser)){
            Following following= Following.builder()
                    .followingFrom(LocalDateTime.now(clock))
                    .follower(loggedUser)
                    .following(followingUser)
                    .build();

            followingRepository.save(following);

            loggedUser.setFollowingNumber(loggedUser.getFollowingNumber()+1);
            userRepository.save(loggedUser);

            followingUser.setFollowersNumber(followingUser.getFollowersNumber()+1);
            userRepository.save(followingUser);

            notificationService.notifyFollow(followingUser);
            return setTheOnlyFollowing(following);
        }
        else
            throw new IllegalAccessException("You are already following this user");
    }

    @Override
    @Transactional
    public void unfollow(String id) throws IllegalAccessException {
        User loggedUser = jwtContextUtils.getUserLoggedFromContext();

        if(loggedUser.getId().equals(id))
            throw new IllegalAccessException("Invalid id, can't unfollow yourself");

        User followingUser = userRepository.findById(id).orElseThrow(EntityNotFoundException::new);

        Following following = followingRepository.findByFollowerEqualsAndFollowingEquals(loggedUser, followingUser);

        if(following==null)
            throw new IllegalArgumentException("Can't find following relationship");

        followingRepository.delete(following);

        followingUser.setFollowersNumber(followingUser.getFollowersNumber()-1);
        userRepository.save(followingUser);

        loggedUser.setFollowingNumber(loggedUser.getFollowingNumber()-1);
        userRepository.save(loggedUser);
    }

    @Override
    public Page<FollowingFollowersDTO> getFollowingByUser(String id, int page, int sizePage) {
        User user = userRepository.findById(id).orElseThrow(EntityNotFoundException::new);

        Page<Following> followingPage = new PageImpl<Following>(user.getFollowing(), PageRequest.of(page,sizePage),user.getFollowing().size());

        List<FollowingFollowersDTO> followingFollowersDTOS = new ArrayList<>();

        for (Following following: followingPage){
            followingFollowersDTOS.add(setTheOnlyFollowing(following)) ;
        }

        return new PageImpl<>(followingFollowersDTOS, PageRequest.of(page,sizePage),followingPage.getTotalElements());
    }

    @Override
    public Page<FollowingFollowersDTO> getFollowersOfUser(String id, int page, int sizePage) {
        User user = userRepository.findById(id).orElseThrow(EntityNotFoundException::new);

        Page<Following> followersPage = new PageImpl<Following>(user.getFollowers(), PageRequest.of(page,sizePage),user.getFollowers().size());

        List<FollowingFollowersDTO> followingFollowersDTOS = new ArrayList<>();

        for (Following following: followersPage){
            followingFollowersDTOS.add(setTheOnlyFollowers(following)) ;
        }

        return new PageImpl<>(followingFollowersDTOS, PageRequest.of(page,sizePage),followersPage.getTotalElements());
    }

    @Override
    public ResponseEntity<Boolean> imFollowingThisUser(String userId) {
        User loggedUser = jwtContextUtils.getUserLoggedFromContext();
        User followingUser = userRepository.findById(userId).orElseThrow(EntityNotFoundException::new);
        return ResponseEntity.ok( followingRepository.existsFollowingByFollowerEqualsAndFollowingEquals(loggedUser,followingUser));

    }

    private FollowingFollowersDTO setTheOnlyFollowing(Following following){
        return FollowingFollowersDTO.builder()
                .id(following.getId())
                .following(modelMapper.map(following.getFollowing(), UserBasicDTO.class))
                .followingFrom(following.getFollowingFrom())
                .build();

    }

    private FollowingFollowersDTO setTheOnlyFollowers(Following following){
        return FollowingFollowersDTO.builder()
                .id(following.getId())
                .follower(modelMapper.map(following.getFollower(), UserBasicDTO.class))
                .followingFrom(following.getFollowingFrom())
                .build();
    }
}
