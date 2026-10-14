package com.splitxpence;

import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.Optional;

import org.springframework.web.bind.annotation.*;

@RestController
public class GroupController {

    private final GroupRepository groupRepository;
    private final UserRepository userRepository;

    public GroupController(
            GroupRepository groupRepository,
            UserRepository userRepository) {

        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
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
}