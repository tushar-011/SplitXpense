package com.splitxpence.controller;

import com.splitxpence.model.Expense;
import com.splitxpence.model.ExpenseGroup;

import com.splitxpence.repository.ExpenseRepository;
import com.splitxpence.repository.GroupMemberRepository;
import com.splitxpence.repository.GroupRepository;

import jakarta.servlet.http.HttpSession;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HistoryController {

    private final ExpenseRepository expenseRepository;
    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;

    public HistoryController(
            ExpenseRepository expenseRepository,
            GroupRepository groupRepository,
            GroupMemberRepository groupMemberRepository) {

        this.expenseRepository = expenseRepository;
        this.groupRepository = groupRepository;
        this.groupMemberRepository = groupMemberRepository;
    }

    @GetMapping("/expense-history")
    public Object expenseHistory(
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

        boolean isCreator =
                group.getCreatedBy()
                        .getId()
                        .equals(userId);

        boolean isMember =
                groupMemberRepository
                        .findByGroupIdAndUserId(
                                groupId,
                                userId
                        )
                        .isPresent();

        if (!isCreator && !isMember) {
            return "You are not part of this group";
        }

        List<Expense> expenses =
                expenseRepository.findByGroupId(groupId);

        expenses.sort(
                Comparator.comparing(
                        Expense::getExpenseDate
                ).reversed()
        );

        return expenses;
    }
}