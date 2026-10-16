package com.splitxpence.controller;

import com.splitxpence.model.ExpenseGroup;
import com.splitxpence.model.Friend;
import com.splitxpence.model.GroupMember;
import com.splitxpence.model.User;

import com.splitxpence.repository.ExpenseRepository;
import com.splitxpence.repository.FriendRepository;
import com.splitxpence.repository.GroupMemberRepository;
import com.splitxpence.repository.GroupRepository;
import com.splitxpence.repository.SettlementRepository;
import com.splitxpence.repository.UserRepository;

import jakarta.servlet.http.HttpSession;

import java.util.List;
import java.util.Optional;

import org.springframework.web.bind.annotation.*;

@RestController
public class GroupController {

    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final ExpenseRepository expenseRepository;
    private final SettlementRepository settlementRepository;
    private final FriendRepository friendRepository;

    public GroupController(
            GroupRepository groupRepository,
            UserRepository userRepository,
            GroupMemberRepository groupMemberRepository,
            ExpenseRepository expenseRepository,
            SettlementRepository settlementRepository,
            FriendRepository friendRepository) {

        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
        this.groupMemberRepository = groupMemberRepository;
        this.expenseRepository = expenseRepository;
        this.settlementRepository = settlementRepository;
        this.friendRepository = friendRepository;
    }

    @PostMapping("/create-group")
    public String createGroup(
            @RequestParam String name,
            @RequestParam String description,
            @RequestParam(required = false) List<Long> friendIds,
            HttpSession session) {

        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) {
            return "Please login first";
        }

        Optional<User> userOptional =
                userRepository.findById(userId);

        if (userOptional.isEmpty()) {
            return "User not found";
        }

        User creator = userOptional.get();

        ExpenseGroup group =
                new ExpenseGroup(
                        name,
                        description,
                        creator
                );

        groupRepository.save(group);

        if (friendIds != null) {

            for (Long friendId : friendIds) {

                Optional<Friend> friendship =
                        friendRepository
                                .findByUserIdAndFriendId(
                                        userId,
                                        friendId
                                );

                if (friendship.isEmpty()) {
                    continue;
                }

                Optional<User> friendUser =
                        userRepository.findById(friendId);

                if (friendUser.isPresent()) {

                    GroupMember member =
                            new GroupMember(
                                    group,
                                    friendUser.get()
                            );

                    groupMemberRepository.save(member);
                }
            }
        }

        return "Group created successfully";
    }

    @GetMapping("/groups")
    public List<ExpenseGroup> viewGroups(
            HttpSession session) {

        Long userId =
                (Long) session.getAttribute("userId");

        if (userId == null) {
            return List.of();
        }

        return groupRepository
                .findByCreatedById(userId);
    }

    @PostMapping("/edit-group")
    public String editGroup(
            @RequestParam Long groupId,
            @RequestParam String name,
            @RequestParam String description,
            HttpSession session) {

        Long userId =
                (Long) session.getAttribute("userId");

        if (userId == null) {
            return "Please login first";
        }

        Optional<ExpenseGroup> groupOptional =
                groupRepository.findById(groupId);

        if (groupOptional.isEmpty()) {
            return "Group not found";
        }

        ExpenseGroup group =
                groupOptional.get();

        if (!group.getCreatedBy()
                .getId()
                .equals(userId)) {

            return "Only group creator can edit this group";
        }

        group.setName(name);
        group.setDescription(description);

        groupRepository.save(group);

        return "Group updated successfully";
    }

    @PostMapping("/delete-group")
    public String deleteGroup(
            @RequestParam Long groupId,
            HttpSession session) {

        Long userId =
                (Long) session.getAttribute("userId");

        if (userId == null) {
            return "Please login first";
        }

        Optional<ExpenseGroup> groupOptional =
                groupRepository.findById(groupId);

        if (groupOptional.isEmpty()) {
            return "Group not found";
        }

        ExpenseGroup group =
                groupOptional.get();

        if (!group.getCreatedBy()
                .getId()
                .equals(userId)) {

            return "Only group creator can delete this group";
        }

        if (!expenseRepository
                .findByGroupId(groupId)
                .isEmpty()) {

            return "Cannot delete group because it contains expenses";
        }

        if (!settlementRepository
                .findByGroupId(groupId)
                .isEmpty()) {

            return "Cannot delete group because it contains settlements";
        }

        List<GroupMember> members =
                groupMemberRepository
                        .findByGroupId(groupId);

        groupMemberRepository.deleteAll(members);

        groupRepository.delete(group);

        return "Group deleted successfully";
    }

    @PostMapping("/add-member")
    public String addMember(
            @RequestParam Long groupId,
            @RequestParam String email,
            HttpSession session) {

        Long userId =
                (Long) session.getAttribute("userId");

        if (userId == null) {
            return "Please login first";
        }

        Optional<ExpenseGroup> groupOptional =
                groupRepository.findById(groupId);

        if (groupOptional.isEmpty()) {
            return "Group not found";
        }

        ExpenseGroup group =
                groupOptional.get();

        if (!group.getCreatedBy()
                .getId()
                .equals(userId)) {

            return "Only group creator can add members";
        }

        Optional<User> userOptional =
                userRepository.findByEmail(email);

        if (userOptional.isEmpty()) {
            return "User not found";
        }

        User user =
                userOptional.get();

        Optional<GroupMember> existing =
                groupMemberRepository
                        .findByGroupIdAndUserId(
                                groupId,
                                user.getId()
                        );

        if (existing.isPresent()) {
            return "User is already a member";
        }

        groupMemberRepository.save(
                new GroupMember(group, user)
        );

        return "Member added successfully";
    }

    @GetMapping("/group-members")
    public List<GroupMember> viewMembers(
            @RequestParam Long groupId) {

        return groupMemberRepository
                .findByGroupId(groupId);
    }

    @PostMapping("/remove-member")
    public String removeMember(
            @RequestParam Long groupId,
            @RequestParam Long userId,
            HttpSession session) {

        Long loggedInUserId =
                (Long) session.getAttribute("userId");

        if (loggedInUserId == null) {
            return "Please login first";
        }

        Optional<ExpenseGroup> groupOptional =
                groupRepository.findById(groupId);

        if (groupOptional.isEmpty()) {
            return "Group not found";
        }

        ExpenseGroup group =
                groupOptional.get();

        if (!group.getCreatedBy()
                .getId()
                .equals(loggedInUserId)) {

            return "Only group creator can remove members";
        }

        Optional<GroupMember> memberOptional =
                groupMemberRepository
                        .findByGroupIdAndUserId(
                                groupId,
                                userId
                        );

        if (memberOptional.isEmpty()) {
            return "Member not found";
        }

        groupMemberRepository
                .delete(memberOptional.get());

        return "Member removed successfully";
    }
}