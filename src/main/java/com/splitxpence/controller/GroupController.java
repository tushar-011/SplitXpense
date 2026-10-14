package com.splitxpence.controller;

import com.splitxpence.repository.GroupMemberRepository;
import com.splitxpence.repository.GroupRepository;
import com.splitxpence.repository.UserRepository;
import com.splitxpence.model.GroupMember;
import com.splitxpence.model.ExpenseGroup;
import com.splitxpence.model.User;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.Optional;

import org.springframework.web.bind.annotation.*;

@RestController
public class GroupController {

    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final GroupMemberRepository groupMemberRepository;

    public GroupController(
            GroupRepository groupRepository,
            UserRepository userRepository,
            GroupMemberRepository groupMemberRepository) {

        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
        this.groupMemberRepository = groupMemberRepository;
    }

    @PostMapping("/create-group")
    public String createGroup(
            @RequestParam String name,
            @RequestParam String description,
            HttpSession session) {

        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) {
            return "Please login first";
        }

        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional.isEmpty()) {
            return "User not found";
        }

        ExpenseGroup group = new ExpenseGroup(
                name,
                description,
                userOptional.get()
        );

        groupRepository.save(group);

        return "Group created successfully";
    }

    @GetMapping("/groups")
    public List<ExpenseGroup> viewGroups(HttpSession session) {

        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) {
            return List.of();
        }

        return groupRepository.findByCreatedById(userId);
    }

    @PostMapping("/add-member")
    public String addMember(
            @RequestParam Long groupId,
            @RequestParam String email,
            HttpSession session) {

        Long loggedInUserId = (Long) session.getAttribute("userId");

        if (loggedInUserId == null) {
            return "Please login first";
        }

        Optional<ExpenseGroup> groupOptional =
                groupRepository.findById(groupId);

        if (groupOptional.isEmpty()) {
            return "Group not found";
        }

        ExpenseGroup group = groupOptional.get();

        if (!group.getCreatedBy().getId().equals(loggedInUserId)) {
            return "Only group creator can add members";
        }

        Optional<User> userOptional =
                userRepository.findByEmail(email);

        if (userOptional.isEmpty()) {
            return "User not found";
        }

        User user = userOptional.get();

        Optional<GroupMember> existingMember =
                groupMemberRepository
                        .findByGroupIdAndUserId(groupId, user.getId());

        if (existingMember.isPresent()) {
            return "User is already a member";
        }

        GroupMember member = new GroupMember(group, user);

        groupMemberRepository.save(member);

        return "Member added successfully";
    }

    @GetMapping("/group-members")
    public List<GroupMember> viewMembers(
            @RequestParam Long groupId) {

        return groupMemberRepository.findByGroupId(groupId);
    }

    @PostMapping("/remove-member")
    public String removeMember(
            @RequestParam Long groupId,
            @RequestParam Long userId,
            HttpSession session) {

        Long loggedInUserId = (Long) session.getAttribute("userId");

        if (loggedInUserId == null) {
            return "Please login first";
        }

        Optional<ExpenseGroup> groupOptional =
                groupRepository.findById(groupId);

        if (groupOptional.isEmpty()) {
            return "Group not found";
        }

        ExpenseGroup group = groupOptional.get();

        if (!group.getCreatedBy().getId().equals(loggedInUserId)) {
            return "Only group creator can remove members";
        }

        Optional<GroupMember> memberOptional =
                groupMemberRepository
                        .findByGroupIdAndUserId(groupId, userId);

        if (memberOptional.isEmpty()) {
            return "Member not found";
        }

        groupMemberRepository.delete(memberOptional.get());

        return "Member removed successfully";
    }
}