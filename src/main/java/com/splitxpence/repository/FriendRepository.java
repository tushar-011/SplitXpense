package com.splitxpence.repository;

import com.splitxpence.model.Friend;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FriendRepository
        extends JpaRepository<Friend, Long> {

    List<Friend> findByUserId(
            Long userId
    );


    Optional<Friend> findByUserIdAndFriendId(
            Long userId,
            Long friendId
    );
}