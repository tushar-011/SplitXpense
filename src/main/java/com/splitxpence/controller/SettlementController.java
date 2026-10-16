package com.splitxpence.controller;

import com.splitxpence.model.ExpenseGroup;
import com.splitxpence.model.Settlement;
import com.splitxpence.model.User;

import com.splitxpence.repository.GroupMemberRepository;
import com.splitxpence.repository.GroupRepository;
import com.splitxpence.repository.SettlementRepository;
import com.splitxpence.repository.UserRepository;

import jakarta.servlet.http.HttpSession;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.web.bind.annotation.*;

@RestController
public class SettlementController {

    private final SettlementRepository settlementRepository;
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final GroupMemberRepository groupMemberRepository;

    public SettlementController(
            SettlementRepository settlementRepository,
            GroupRepository groupRepository,
            UserRepository userRepository,
            GroupMemberRepository groupMemberRepository) {

        this.settlementRepository = settlementRepository;
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
        this.groupMemberRepository = groupMemberRepository;
    }

    @PostMapping("/record-settlement")
    public String recordSettlement(
            @RequestParam Long groupId,
            @RequestParam String fromEmail,
            @RequestParam String toEmail,
            @RequestParam double amount,
            @RequestParam String settlementDate,
            HttpSession session) {

        Long loggedInUserId =
                (Long) session.getAttribute("userId");

        if (loggedInUserId == null) {
            return "Please login first";
        }

        if (amount <= 0) {
            return "Settlement amount must be greater than zero";
        }

        Optional<ExpenseGroup> groupOptional =
                groupRepository.findById(groupId);

        if (groupOptional.isEmpty()) {
            return "Group not found";
        }

        ExpenseGroup group =
                groupOptional.get();

        boolean loggedInUserIsCreator =
                group.getCreatedBy()
                        .getId()
                        .equals(loggedInUserId);

        boolean loggedInUserIsMember =
                groupMemberRepository
                        .findByGroupIdAndUserId(
                                groupId,
                                loggedInUserId
                        )
                        .isPresent();

        if (!loggedInUserIsCreator
                && !loggedInUserIsMember) {

            return "You are not part of this group";
        }

        Optional<User> fromOptional =
                userRepository.findByEmail(fromEmail);

        Optional<User> toOptional =
                userRepository.findByEmail(toEmail);

        if (fromOptional.isEmpty()) {
            return "Paying user not found";
        }

        if (toOptional.isEmpty()) {
            return "Receiving user not found";
        }

        User fromUser =
                fromOptional.get();

        User toUser =
                toOptional.get();

        if (fromUser.getId()
                .equals(toUser.getId())) {

            return "A user cannot settle with themselves";
        }

        boolean fromIsCreator =
                group.getCreatedBy()
                        .getId()
                        .equals(fromUser.getId());

        boolean fromIsMember =
                groupMemberRepository
                        .findByGroupIdAndUserId(
                                groupId,
                                fromUser.getId()
                        )
                        .isPresent();

        boolean toIsCreator =
                group.getCreatedBy()
                        .getId()
                        .equals(toUser.getId());

        boolean toIsMember =
                groupMemberRepository
                        .findByGroupIdAndUserId(
                                groupId,
                                toUser.getId()
                        )
                        .isPresent();

        if (!fromIsCreator && !fromIsMember) {
            return "Paying user is not part of this group";
        }

        if (!toIsCreator && !toIsMember) {
            return "Receiving user is not part of this group";
        }

        Settlement settlement =
                new Settlement(
                        group,
                        fromUser,
                        toUser,
                        amount,
                        LocalDate.parse(settlementDate)
                );

        settlementRepository.save(settlement);

        return "Settlement recorded successfully";
    }

    @GetMapping("/settlement-history")
    public List<Settlement> settlementHistory(
            @RequestParam Long groupId,
            HttpSession session) {

        Long loggedInUserId =
                (Long) session.getAttribute("userId");

        if (loggedInUserId == null) {
            return List.of();
        }

        return settlementRepository
                .findByGroupId(groupId);
    }
}