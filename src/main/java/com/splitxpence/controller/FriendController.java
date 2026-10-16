package com.splitxpence.controller;

import com.splitxpence.model.Friend;
import com.splitxpence.model.User;

import com.splitxpence.repository.FriendRepository;
import com.splitxpence.repository.UserRepository;

import jakarta.servlet.http.HttpSession;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.web.bind.annotation.*;

@RestController
public class FriendController {

    private final FriendRepository friendRepository;
    private final UserRepository userRepository;


    public FriendController(
            FriendRepository friendRepository,
            UserRepository userRepository) {

        this.friendRepository =
                friendRepository;

        this.userRepository =
                userRepository;
    }


    @PostMapping("/add-friend")
    public String addFriend(
            @RequestParam String email,
            HttpSession session) {

        Long userId =
                (Long) session.getAttribute(
                        "userId"
                );


        if (userId == null) {

            return "Please login first";
        }


        Optional<User> currentUserOptional =
                userRepository.findById(
                        userId
                );


        if (currentUserOptional.isEmpty()) {

            return "User not found";
        }


        Optional<User> friendOptional =
                userRepository.findByEmail(
                        email
                );


        if (friendOptional.isEmpty()) {

            return "No registered user found with this email";
        }


        User currentUser =
                currentUserOptional.get();


        User friendUser =
                friendOptional.get();


        if (currentUser.getId()
                .equals(friendUser.getId())) {

            return "You cannot add yourself as a friend";
        }


        Optional<Friend> existingFriend =
                friendRepository
                        .findByUserIdAndFriendId(
                                currentUser.getId(),
                                friendUser.getId()
                        );


        if (existingFriend.isPresent()) {

            return "This user is already your friend";
        }


        Friend friend =
                new Friend(
                        currentUser,
                        friendUser
                );


        friendRepository.save(friend);


        return "Friend added successfully";
    }


    @GetMapping("/friends")
    public Object getFriends(
            HttpSession session) {

        Long userId =
                (Long) session.getAttribute(
                        "userId"
                );


        if (userId == null) {

            return List.of();
        }


        List<Friend> friends =
                friendRepository
                        .findByUserId(userId);


        List<Map<String, Object>> result =
                new ArrayList<>();


        for (Friend relation : friends) {

            User friend =
                    relation.getFriend();


            Map<String, Object> data =
                    new LinkedHashMap<>();


            data.put(
                    "relationId",
                    relation.getId()
            );


            data.put(
                    "userId",
                    friend.getId()
            );


            data.put(
                    "name",
                    friend.getName()
            );


            data.put(
                    "email",
                    friend.getEmail()
            );


            result.add(data);
        }


        return result;
    }


    @PostMapping("/remove-friend")
    public String removeFriend(
            @RequestParam Long relationId,
            HttpSession session) {

        Long userId =
                (Long) session.getAttribute(
                        "userId"
                );


        if (userId == null) {

            return "Please login first";
        }


        Optional<Friend> friendOptional =
                friendRepository.findById(
                        relationId
                );


        if (friendOptional.isEmpty()) {

            return "Friend relation not found";
        }


        Friend relation =
                friendOptional.get();


        if (!relation.getUser()
                .getId()
                .equals(userId)) {

            return "You cannot remove this friend";
        }


        friendRepository.delete(
                relation
        );


        return "Friend removed successfully";
    }
}